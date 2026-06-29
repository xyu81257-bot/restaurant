package com.restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 解决前后端分离跨域问题
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许前端访问地址，开发阶段可先放开
                .allowedOriginPatterns("*")
                // 允许的请求方式
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许携带的请求头
                .allowedHeaders("*")
                // 是否允许携带 cookie
                .allowCredentials(true)
                // 预检请求缓存时间
                .maxAge(3600);
    }
}