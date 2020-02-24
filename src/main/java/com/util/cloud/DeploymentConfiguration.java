package com.util.cloud;

import java.util.Optional;
import java.util.function.Function;

public interface DeploymentConfiguration<T> {

    final Function<String, String> ENV = (key) -> System.getenv().getOrDefault(key, System.getProperty(key));


    @SuppressWarnings("unchecked")
    static<T> T getProperty(String key, T fallback) {
        String type =  fallback != null ? fallback.getClass().getSimpleName().toUpperCase() : "STRING";
        T value = (T) PropertyTypes.valueOf(type).getValue(key, ENV);

        return Optional.ofNullable( value ).orElse( fallback );
    }
}
