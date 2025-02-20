package br.com.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/users/**")
                        .uri("http://localhost:8091"))
                .route("account-service", r -> r.path("/accounts/**")
                        .uri("http://localhost:8092"))
                .route("food-service", r -> r.path("/v1/order/**",
                                "/v1/menu/**", "/v1/customer/**", "/v1/category/**", "/v1/product/**",
                                "/v1/delivery-fees/**")
                        .uri("http://localhost:8093"))
                .build();
    }
}
