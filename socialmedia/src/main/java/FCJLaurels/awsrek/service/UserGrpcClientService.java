package FCJLaurels.awsrek.service;

import FCJ.user.grpc.BlogUserInfoRequest;
import FCJ.user.grpc.BlogUserInfoResponse;
import FCJ.user.grpc.UserInfoServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
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

    private ManagedChannel channel;
    private UserInfoServiceGrpc.UserInfoServiceBlockingStub blockingStub;

    @PostConstruct
    public void init() {
        // Extract host and port from address
        String[] parts = grpcServerAddress.replace("static://", "").split(":");
        String host = parts[0];
        int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 9090;

        log.info("Initializing gRPC channel to {}:{}", host, port);

        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        blockingStub = UserInfoServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        try {
            if (channel != null && !channel.isShutdown()) {
                log.info("Shutting down gRPC channel");
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            log.error("Error shutting down gRPC channel", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Fetch user information (name and avatar) by user ID using gRPC
     *
     * @param userId The user ID to fetch information for
     * @return BlogUserInfoResponse containing name and avatar URL, or null if failed
     */
    public BlogUserInfoResponse getUserInfo(String userId) {
        if (userId == null || userId.isEmpty()) {
            log.warn("User ID is null or empty, returning null");
            return null;
        }

        try {
            log.debug("Fetching user info for userId: {}", userId);

            BlogUserInfoRequest request = BlogUserInfoRequest.newBuilder()
                    .setId(userId)
                    .build();

            BlogUserInfoResponse response = blockingStub.blogUserInfo(request);

            log.debug("Successfully fetched user info - name: {}, avatar: {}",
                    response.getName(), response.getAvatar());

            return response;

        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed for userId {}: {}", userId, e.getStatus());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error fetching user info for userId {}", userId, e);
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
}
