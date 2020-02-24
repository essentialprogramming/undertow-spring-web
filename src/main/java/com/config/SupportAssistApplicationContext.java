package com.config;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public enum SupportAssistApplicationContext {

    INSTANCE;
    private static final String CONFIG_LOCATION = "com";

    public final AnnotationConfigWebApplicationContext getServletContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register( MvcConfig.class);
        context.setConfigLocation(CONFIG_LOCATION);
        return context;
    }

    public final AnnotationConfigWebApplicationContext getAppContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(CONFIG_LOCATION);
        return context;
    }


}