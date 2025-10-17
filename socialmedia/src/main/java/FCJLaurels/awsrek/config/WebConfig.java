package FCJLaurels.awsrek.config;

//import com.Swp_391_gr7.smoking_cessation_support_platform_backend.interceptors.JWTInterceptor;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MetricsInterceptor metricsInterceptor;

//    private final JWTInterceptor jwtInterceptor;
//
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add metrics interceptor to track all requests
        registry.addInterceptor(metricsInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/actuator/**"); // Don't track actuator endpoints
//        registry.addInterceptor(jwtInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns(
//                        "/auth/**",
//                        "/swagger-ui/**",
//                        "/v3/api-docs/**",
//                        "/swagger-ui.html",
//                        "/webjars/**"
//                );
    }
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

//    // Serve OpenAPI / Swagger UI static resources explicitly to avoid issues when security is enabled
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // Swagger UI static resources
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//
//        registry.addResourceHandler("/swagger-ui/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
//
//        // Webjars (used by some swagger builds)
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("API doc")
                        .version("1.0.0")
                        .description("API doc")
        );
    }
}
