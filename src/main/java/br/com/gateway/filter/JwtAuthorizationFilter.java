package br.com.gateway.filter;

import br.com.gateway.client.UserServiceClient;
import br.com.gateway.config.RoutePermissionsConfig;
import br.com.gateway.dto.UserAuthDTO;
import br.com.gateway.enumeration.UserType;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthorizationFilter implements WebFilter {

    private final UserServiceClient userServiceClient;
    private final RoutePermissionsConfig routePermissionsConfig;

    private final List<String> publicRoutes = List.of(
            "/users/auth"
    );

    public JwtAuthorizationFilter(UserServiceClient userServiceClient, RoutePermissionsConfig routePermissionsConfig) {
        this.userServiceClient = userServiceClient;
        this.routePermissionsConfig = routePermissionsConfig;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        String path = exchange.getRequest().getPath().toString();

        if (publicRoutes.contains(path)) {
            return chain.filter(exchange);
        }

        if (token == null || !token.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        token = token.replace("Bearer ", "");

        try {
            // Consultar o serviço de usuários para validar o token e obter o tipo de usuário
            UserAuthDTO userAuthDTO = userServiceClient.validateTokenAndRole("Bearer "+token);

            // Verificar permissões
            if (routePermissionsConfig.isAdminRoute(path) && userAuthDTO.getUserType() == UserType.ADMIN) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
