package FCJLaurels.awsrek.service;

import FCJ.user.grpc.BlogUserInfoRequest;
import FCJ.user.grpc.BlogUserInfoResponse;
import FCJ.user.grpc.UserInfoServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserGrpcClientService {

    @Value("${grpc.client.user-service.address:localhost:9090}")
    private String grpcServerAddress;

    @Value("${grpc.client.user-service.timeout:5}")
    private int grpcTimeoutSeconds;

    private ManagedChannel channel;
    private UserInfoServiceGrpc.UserInfoServiceBlockingStub blockingStub;

    @PostConstruct
    public void init() {
        // Extract host and port from address
        String cleanAddress = grpcServerAddress.replace("static://", "").trim();
        String[] parts = cleanAddress.split(":");
        String host = parts[0].trim();
        int port = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 9090;

        log.info("=================================================================");
        log.info("🚀 Initializing gRPC channel to {}:{} with timeout {}s", host, port, grpcTimeoutSeconds);
        log.info("=================================================================");

        try {
            channel = ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext()
                    .maxRetryAttempts(3)
                    .retryBufferSize(16 * 1024 * 1024) // 16MB
                    .perRpcBufferLimit(1024 * 1024) // 1MB
                    .keepAliveTime(30, TimeUnit.SECONDS)
                    .keepAliveTimeout(5, TimeUnit.SECONDS)
                    .keepAliveWithoutCalls(true)
                    .build();

            blockingStub = UserInfoServiceGrpc.newBlockingStub(channel);

            log.info("✅ gRPC channel initialized successfully");
            log.info("📡 Target: {}:{}", host, port);
            log.info("⏱️  Timeout: {}s", grpcTimeoutSeconds);

            // Perform initial health check
            try {
                boolean healthy = isServerHealthy();
                if (healthy) {
                    log.info("✅ gRPC server is REACHABLE and HEALTHY");
                } else {
                    log.warn("⚠️  gRPC server is NOT REACHABLE - Please check if user-info service is running on {}:{}", host, port);
                }
            } catch (Exception healthEx) {
                log.warn("⚠️  Could not perform initial health check: {}", healthEx.getMessage());
            }

            log.info("=================================================================");
        } catch (Exception e) {
            log.error("❌ Failed to initialize gRPC channel to {}:{}", host, port, e);
            log.error("=================================================================");
            throw new RuntimeException("Failed to initialize gRPC channel", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            if (channel != null && !channel.isShutdown()) {
                log.info("Shutting down gRPC channel");
                if (!channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("gRPC channel did not terminate gracefully");
                    channel.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            log.error("Error shutting down gRPC channel", e);
            if (channel != null) {
                channel.shutdownNow();
            }
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Fetch user information (name and avatar) by user ID using gRPC (BLOCKING)
     * Accepts UUID as string (e.g., "550e8400-e29b-41d4-a716-446655440000")
     *
     * @param userId The user ID to fetch information for (UUID as string)
     * @return BlogUserInfoResponse containing name and avatar URL, or null if failed
     */
    public BlogUserInfoResponse getUserInfo(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            log.warn("❌ User ID is null or empty, returning null");
            return null;
        }

        String trimmedUserId = userId.trim();

        try {
            log.info("🔍 Fetching user info via gRPC for userId: {} (UUID format)", trimmedUserId);

            // Validate UUID format
            try {
                java.util.UUID.fromString(trimmedUserId);
                log.debug("✅ UUID validation passed for: {}", trimmedUserId);
            } catch (IllegalArgumentException e) {
                log.warn("⚠️  Invalid UUID format provided: {}", trimmedUserId);
                // Continue anyway - let the server handle it
            }

            BlogUserInfoRequest request = BlogUserInfoRequest.newBuilder()
                    .setId(trimmedUserId)
                    .build();

            log.debug("📤 Sending gRPC request with UUID: {}", trimmedUserId);
            BlogUserInfoResponse response = blockingStub
                    .withDeadlineAfter(grpcTimeoutSeconds, TimeUnit.SECONDS) // <--- MOVE IT HERE
                    .blogUserInfo(request);
            log.info("🔍 RAW PROTO RESPONSE: {}", response.toString());

            if (response != null) {
                log.debug("✅ Successfully fetched user info - name: '{}', avatar: '{}'",
                        response.getName(), response.getAvatar());

                // Additional validation
                if (response.getName() == null || response.getName().isEmpty()) {
                    log.warn("⚠️  User info fetched but name is empty for userId: {}", userId);
                }
                if (response.getAvatar() == null || response.getAvatar().isEmpty()) {
                    log.debug("ℹ️  User info fetched but avatar is empty for userId: {}", userId);
                }
            } else {
                log.warn("⚠️  gRPC returned null response for userId: {}", userId);
            }

            return response;

        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();

            if (code == Status.DEADLINE_EXCEEDED.getCode()) {
                log.error("⏱️  gRPC call TIMEOUT ({}s exceeded) for userId: {}", grpcTimeoutSeconds, userId);
                log.error("💡 TIP: Check if user-info service is slow or check network latency");
            } else if (code == Status.UNAVAILABLE.getCode()) {
                log.error("🔌 gRPC server UNAVAILABLE for userId: {} - Is the service running on {}?",
                        userId, grpcServerAddress);
                log.error("💡 TIP: Verify user-info service is running with: netstat -ano | findstr :9090");
            } else if (code == Status.NOT_FOUND.getCode()) {
                log.warn("🔍 User NOT FOUND for userId: {} - User may not exist in database", userId);
            } else {
                log.error("❌ gRPC call failed for userId: {} - Status: {} - Message: {}",
                        userId, e.getStatus().getCode(), e.getStatus().getDescription());
            }

            return null;
        } catch (Exception e) {
            log.error("❌ Unexpected error fetching user info for userId: {}", userId, e);
            log.error("💡 TIP: Check application logs and gRPC server logs");
            return null;
        }
    }

    /**
     * Get user name by user ID
     *
     * @param userId The user ID
     * @return User's full name or null if not found
     */
    public String getUserName(String userId) {
        BlogUserInfoResponse response = getUserInfo(userId);
        return response != null ? response.getName() : null;
    }

    /**
     * Get user avatar URL by user ID
     *
     * @param userId The user ID
     * @return User's avatar URL or null if not found
     */
    public String getUserAvatarUrl(String userId) {
        BlogUserInfoResponse response = getUserInfo(userId);
        return response != null ? response.getAvatar() : null;
    }

    /**
     * Health check - verify if gRPC server is reachable
     *
     * @return true if server is reachable, false otherwise
     */
    public boolean isServerHealthy() {
        try {
            log.debug("🏥 Performing gRPC health check...");

            // FIX: Use a valid UUID format (Zeros) so the server parsing passes
            // but the DB lookup fails (NOT_FOUND).
            BlogUserInfoRequest request = BlogUserInfoRequest.newBuilder()
                    .setId("00000000-0000-0000-0000-000000000000")
                    .build();

            UserInfoServiceGrpc.UserInfoServiceBlockingStub healthCheckStub =
                    UserInfoServiceGrpc.newBlockingStub(channel)
                            .withDeadlineAfter(2, TimeUnit.SECONDS);

            // This will throw an exception if the server is down
            // We expect NOT_FOUND which means server is up but user doesn't exist
            try {
                healthCheckStub.blogUserInfo(request);
                log.debug("✅ Health check: Server is UP and responding");
                return true;
            } catch (StatusRuntimeException e) {
                // NOT_FOUND means server is UP (it responded), just user doesn't exist
                if (e.getStatus().getCode() == Status.NOT_FOUND.getCode()) {
                    log.debug("✅ Health check: Server is UP (responded with NOT_FOUND as expected)");
                    return true;
                }
                throw e; // Re-throw other errors
            }
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.UNAVAILABLE.getCode()) {
                log.warn("❌ Health check FAILED: Server UNAVAILABLE at {}", grpcServerAddress);
                log.warn("💡 Is the user-info service running? Check with: netstat -ano | findstr :9090");
            } else if (e.getStatus().getCode() == Status.DEADLINE_EXCEEDED.getCode()) {
                log.warn("❌ Health check FAILED: TIMEOUT - Server is slow or not responding");
            } else {
                log.warn("❌ Health check FAILED: {} - {}", e.getStatus().getCode(), e.getStatus().getDescription());
            }
            return false;
        } catch (Exception e) {
            log.error("❌ Health check ERROR: {}", e.getMessage());
            return false;
        }
    }
}
