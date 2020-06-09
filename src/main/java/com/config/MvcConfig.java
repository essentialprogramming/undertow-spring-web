package com.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"com.controller"})
public class MvcConfig implements WebMvcConfigurer {

    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    // Add the WEB-INF view resolver.
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("WEB-INF/",".jsp");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry
                .addViewController("/test")
                .setViewName("test");
    }

}
