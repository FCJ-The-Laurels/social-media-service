package FCJLaurels.awsrek.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to track metrics for all HTTP requests
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsInterceptor implements HandlerInterceptor {

    private final MeterRegistry meterRegistry;
    private static final String TIMER_ATTRIBUTE = "request_timer";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Timer.Sample sample = Timer.start(meterRegistry);
        request.setAttribute(TIMER_ATTRIBUTE, sample);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Timer.Sample sample = (Timer.Sample) request.getAttribute(TIMER_ATTRIBUTE);

        if (sample != null) {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();

            // Record request duration
            sample.stop(Timer.builder("http.server.requests.custom")
                    .description("HTTP request duration")
                    .tag("method", method)
                    .tag("uri", uri)
                    .tag("status", String.valueOf(status))
                    .register(meterRegistry));

            // Track request count
            Counter.builder("http.requests.total")
                    .description("Total HTTP requests")
                    .tag("method", method)
                    .tag("uri", uri)
                    .tag("status", String.valueOf(status))
                    .register(meterRegistry)
                    .increment();

            // Track errors
            if (status >= 400) {
                Counter.builder("http.requests.errors")
                        .description("HTTP request errors")
                        .tag("method", method)
                        .tag("uri", uri)
                        .tag("status", String.valueOf(status))
                        .register(meterRegistry)
                        .increment();
            }

            log.debug("Metrics tracked for {} {} - Status: {}", method, uri, status);
        }
    }
}

