package br.com.gateway.config;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class RoutePermissionsConfig {

    private final Set<String> adminRoutes = new HashSet<>();

    public RoutePermissionsConfig() {
        adminRoutes.add("/users/all");
        adminRoutes.add("/users/admins");
    }

    public boolean isAdminRoute(String path) {
        return adminRoutes.stream().anyMatch(path::startsWith);
    }
}
