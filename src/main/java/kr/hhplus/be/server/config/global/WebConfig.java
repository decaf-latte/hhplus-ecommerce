package kr.hhplus.be.server.config.global;

import kr.hhplus.be.server.config.global.LoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
            .addPathPatterns("/**") // 모든 경로에 대해 적용
            .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**"); // 특정 경로 제외
    }
}
