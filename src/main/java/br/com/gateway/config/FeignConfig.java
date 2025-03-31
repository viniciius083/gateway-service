package br.com.gateway.config;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
public class FeignConfig {

    @Bean
    public HttpMessageConverters messageConverters() {
        Collection<HttpMessageConverter<?>> converters = new ArrayList<>();
        return new HttpMessageConverters(converters);
    }
}
