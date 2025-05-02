package org.example.doantn.Security;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Áp dụng cho tất cả các endpoint
                .allowedOrigins("http://localhost:4200")  // Đảm bảo rằng chỉ có frontend của bạn có thể truy cập API
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Các phương thức HTTP được phép
                .allowedHeaders("*")  // Cho phép tất cả các headers
                .allowCredentials(true);  // Nếu cần gửi cookies hoặc authorization headers
    }
}


