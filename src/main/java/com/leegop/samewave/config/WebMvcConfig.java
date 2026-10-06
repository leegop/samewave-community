package com.leegop.samewave.config;

import com.leegop.samewave.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 默认拦截全部
                .addPathPatterns("/**")
                // 白名单：注册、登录不需要登录
                .excludePathPatterns(
                        "/api/user/register",
                        "/api/user/login",
                        "/error"
                );
    }
}