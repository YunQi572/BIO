package com.example.bio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // 允许的前端地址列表（推荐使用模式匹配）
                        .allowedOriginPatterns(
                                "http://localhost:9876",
                                "http://localhost:9877",
                                "http://47.110.247.24:9876",
                                "http://47.122.127.146:9876", // 添加你的前端域名
                                "http://whucsmentorguide.asia" // 如果有域名也要添加
                )
                        // 允许的 HTTP 方法
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        // 允许的请求头（包含自定义头部）
                        .allowedHeaders("*")
                        // 是否允许携带凭证（cookies等）
                        .allowCredentials(true)
                        // 预检请求缓存时间（秒）
                        .maxAge(3600);
            }
        };
    }
}
