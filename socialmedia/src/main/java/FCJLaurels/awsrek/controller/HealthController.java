package FCJLaurels.awsrek.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Health and actuator endpoints for AWS API Gateway")
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    public HealthController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    /**
     * Basic health check endpoint for AWS API Gateway
     * Returns 200 OK if service is UP, 503 if DOWN
     */
    @GetMapping("/ping")
    @Operation(
        summary = "Simple Ping Health Check",
        description = "AWS API Gateway compatible simple health check. Use this for basic ALB/API Gateway health checks."
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy",
        content = @Content(schema = @Schema(implementation = HealthResponse.class)))
    @ApiResponse(responseCode = "503", description = "Service is unhealthy")
    public ResponseEntity<HealthResponse> ping() {
        HealthResponse response = new HealthResponse();
        response.setStatus("UP");
        response.setTimestamp(getCurrentTimestamp());
        response.setService("user-service");
        response.setVersion("1.0.0");

        return ResponseEntity.ok(response);
    }

    /**
     * Detailed health check endpoint
     * Returns comprehensive health information about all components
     */
    @GetMapping("/detailed")
    @Operation(
        summary = "Detailed Health Check",
        description = "Returns detailed health information including database and component statuses"
    )
    @ApiResponse(responseCode = "200", description = "Health check completed successfully")
    public ResponseEntity<DetailedHealthResponse> detailedHealth() {
        HealthComponent health = healthEndpoint.health();

        DetailedHealthResponse response = new DetailedHealthResponse();
        response.setStatus(health.getStatus().toString());
        response.setTimestamp(getCurrentTimestamp());
        response.setService("user-service");
        response.setVersion("1.0.0");

        if (health instanceof Health h) {
            Map<String, Object> details = new LinkedHashMap<>(h.getDetails());
            response.setComponents(details);
        }

        HttpStatus status = "UP".equals(health.getStatus().toString())
            ? HttpStatus.OK
            : HttpStatus.SERVICE_UNAVAILABLE;

        return new ResponseEntity<>(response, status);
    }

    /**
     * Liveness probe for Kubernetes/AWS ECS
     * Indicates if the service should be restarted
     */
    @GetMapping("/live")
    @Operation(
        summary = "Liveness Probe",
        description = "Kubernetes/ECS liveness probe. Returns 200 if service is alive (not frozen)."
    )
    @ApiResponse(responseCode = "200", description = "Service is alive")
    @ApiResponse(responseCode = "503", description = "Service needs restart")
    public ResponseEntity<ProbeResponse> liveness() {
        HealthComponent health = healthEndpoint.health();
        String status = health.getStatus().toString();

        ProbeResponse response = new ProbeResponse();
        response.setStatus(status);
        response.setTimestamp(getCurrentTimestamp());
        response.setProbe("liveness");

        HttpStatus httpStatus = "UP".equals(status) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * Readiness probe for Kubernetes/AWS ECS
     * Indicates if the service is ready to accept traffic
     */
    @GetMapping("/ready")
    @Operation(
        summary = "Readiness Probe",
        description = "Kubernetes/ECS readiness probe. Returns 200 if service is ready to handle requests."
    )
    @ApiResponse(responseCode = "200", description = "Service is ready")
    @ApiResponse(responseCode = "503", description = "Service is not ready yet")
    public ResponseEntity<ProbeResponse> readiness() {
        HealthComponent health = healthEndpoint.health();
        String status = health.getStatus().toString();

        ProbeResponse response = new ProbeResponse();
        response.setStatus(status);
        response.setTimestamp(getCurrentTimestamp());
        response.setProbe("readiness");

        HttpStatus httpStatus = "UP".equals(status) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * Combined health response for AWS API Gateway
     */
    @GetMapping("")
    @Operation(
        summary = "Combined Health Status",
        description = "Returns combined health status for AWS API Gateway and monitoring services"
    )
    @ApiResponse(responseCode = "200", description = "Health status returned",
        content = @Content(schema = @Schema(implementation = HealthResponse.class)))
    public ResponseEntity<HealthResponse> health() {
        HealthComponent health = healthEndpoint.health();
        String status = health.getStatus().toString();

        HealthResponse response = new HealthResponse();
        response.setStatus(status);
        response.setTimestamp(getCurrentTimestamp());
        response.setService("user-service");
        response.setVersion("1.0.0");

        HttpStatus httpStatus = "UP".equals(status) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(response, httpStatus);
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // DTO Classes
    public static class HealthResponse {
        private String status;
        private String timestamp;
        private String service;
        private String version;

        public HealthResponse() {}

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    public static class DetailedHealthResponse {
        private String status;
        private String timestamp;
        private String service;
        private String version;
        private Map<String, Object> components;

        public DetailedHealthResponse() {}

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Map<String, Object> getComponents() {
            return components;
        }

        public void setComponents(Map<String, Object> components) {
            this.components = components;
        }
    }

    public static class ProbeResponse {
        private String status;
        private String timestamp;
        private String probe;

        public ProbeResponse() {}

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getProbe() {
            return probe;
        }

        public void setProbe(String probe) {
            this.probe = probe;
        }
    }
}


