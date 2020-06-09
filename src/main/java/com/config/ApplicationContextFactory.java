package com.config;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class ApplicationContextFactory {

    private static final String SPRING_CONFIG_LOCATION = "com.config";


    private ApplicationContextFactory() {}

    private static class ApplicationContextHolder {
        static final WebApplicationContext CONTEXT = createSpringWebAppContext(SPRING_CONFIG_LOCATION);
    }


    private static  AnnotationConfigWebApplicationContext createSpringWebAppContext(String configLocation) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(configLocation);
        context.register(
                org.springdoc.core.SwaggerUiConfigProperties.class, org.springdoc.core.SwaggerUiOAuthProperties.class,
                org.springdoc.core.SpringDocConfiguration.class, org.springdoc.core.SpringDocConfigProperties.class,
                org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class);
        return context;
    }

    public static WebApplicationContext getSpringApplicationContext(){
        return ApplicationContextHolder.CONTEXT;
    }


}