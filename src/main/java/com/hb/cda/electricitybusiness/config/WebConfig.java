package com.hb.cda.electricitybusiness.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Rendre le dossier './uploads/' accessible via l'URL '/uploads/**'
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");

        // Rendre le dossier 'src/main/resources/static/images/' accessible via l'URL '/images/**'

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}
