package com.example.rbs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        
        corsRegistry.addMapping("/**")
        	.allowedOrigins("http://localhost:3000", "http://192.168.0.7:3000")
        	.allowCredentials(true)
        	.allowedMethods("*");
    }
}