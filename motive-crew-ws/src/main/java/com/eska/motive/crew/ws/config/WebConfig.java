package com.eska.motive.crew.ws.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web configuration for serving static files
 * 
 * @author Motive Crew Team
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get the uploads directory path
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        
        // Map /uploads/** to the uploads directory on the filesystem
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + File.separator);
    }
}

