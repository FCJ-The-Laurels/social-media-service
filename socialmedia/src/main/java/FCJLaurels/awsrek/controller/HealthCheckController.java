package FCJLaurels.awsrek.controller;

import FCJLaurels.awsrek.service.UserGrpcClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Health check and diagnostics endpoints")
@Slf4j
public class HealthCheckController {

    @Autowired
    private UserGrpcClientService userGrpcClientService;

    @GetMapping("/grpc")
    @Operation(summary = "Check gRPC service connectivity")
    public ResponseEntity<Map<String, Object>> checkGrpcHealth() {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean isHealthy = userGrpcClientService.isServerHealthy();
            response.put("status", isHealthy ? "UP" : "DOWN");
            response.put("service", "user-info-grpc");
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            if (isHealthy) {
                log.info("gRPC health check: SUCCESS");
                return ResponseEntity.ok(response);
            } else {
                log.error("gRPC health check: FAILED - Service not responding");
                response.put("error", "gRPC service is not responding on configured address");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            }
        } catch (Exception e) {
            log.error("gRPC health check: EXCEPTION - {}", e.getMessage(), e);
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/grpc/test/{userId}")
    @Operation(summary = "Test gRPC user info fetch")
    public ResponseEntity<Map<String, Object>> testGrpcUserFetch(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("Testing gRPC fetch for userId: {}", userId);

            FCJ.user.grpc.BlogUserInfoResponse userInfo = userGrpcClientService.getUserInfo(userId);

            if (userInfo != null) {
                response.put("status", "SUCCESS");
                response.put("userId", userId);
                response.put("name", userInfo.getName());
                response.put("avatar", userInfo.getAvatar());
                log.info("gRPC test fetch SUCCESS - name: {}, avatar: {}", userInfo.getName(), userInfo.getAvatar());
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "FAILED");
                response.put("userId", userId);
                response.put("error", "User info returned null - user may not exist or service unavailable");
                log.warn("gRPC test fetch returned null for userId: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("userId", userId);
            response.put("error", e.getMessage());
            response.put("stackTrace", e.getClass().getName());
            log.error("gRPC test fetch ERROR for userId: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/system")
    @Operation(summary = "Get system information")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", java.time.LocalDateTime.now().toString());
        response.put("javaVersion", System.getProperty("java.version"));
        response.put("osName", System.getProperty("os.name"));
        response.put("osVersion", System.getProperty("os.version"));
        response.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        response.put("maxMemory", Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB");
        response.put("totalMemory", Runtime.getRuntime().totalMemory() / 1024 / 1024 + " MB");
        response.put("freeMemory", Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB");

        return ResponseEntity.ok(response);
    }
}

