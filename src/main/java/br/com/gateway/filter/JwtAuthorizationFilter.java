package br.com.gateway.filter;

import br.com.gateway.client.UserServiceClient;
import br.com.gateway.config.RoutePermissionsConfig;
import br.com.gateway.dto.UserAuthDTO;
import br.com.gateway.enumeration.UserType;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class JwtAuthorizationFilter implements WebFilter {

    private final UserServiceClient userServiceClient;
    private final RoutePermissionsConfig routePermissionsConfig;


    // Regex para validar UUID
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]+$");


    private static final Map<String, Set<HttpMethod>> PUBLIC_ROUTES = Map.of(
            "/users/auth", Set.of(HttpMethod.POST),
            "/users/register", Set.of(HttpMethod.POST),
            "/v1/menu", Set.of(HttpMethod.GET),
            "/v1/category", Set.of(HttpMethod.GET),
            "/v1/customer", Set.of(HttpMethod.POST),
            "/v1/customer/phone/", Set.of(HttpMethod.GET),
            "/v1/order", Set.of(HttpMethod.POST, HttpMethod.GET)

    );

    public JwtAuthorizationFilter(UserServiceClient userServiceClient, RoutePermissionsConfig routePermissionsConfig) {
        this.userServiceClient = userServiceClient;
        this.routePermissionsConfig = routePermissionsConfig;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        String path = exchange.getRequest().getPath().toString();
        HttpMethod method =  exchange.getRequest().getMethod();

        if (HttpMethod.OPTIONS.name().equals(exchange.getRequest().getMethod().name())) {
            return chain.filter(exchange);
        }

        // Verifica se é rota publica

        if (isPublicRoute(path, method) || isPublicRouteWithUUID(path, method)) {
            log.info("Rota pública: {} - Encaminhando requisição", path);
            return chain.filter(exchange);
        }

        // Verifica se o token é valido
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("Token ausente ou inválido para {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            UserAuthDTO userAuthDTO = userServiceClient.validateTokenAndRole("Bearer "+ token.replace("Bearer ", ""));
            log.info(" Token válido para usuário do tipo: {}", userAuthDTO.getUserType());

            // Verificar permissões
            if (routePermissionsConfig.isAdminRoute(path) && userAuthDTO.getUserType() != UserType.ADMIN) {
                log.info("Acesso negado para: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            Optional<String> headerAccountIdentifier = Optional.ofNullable(headers.getFirst("accountIdentifier"));
            exchange.getRequest().mutate().headers(h -> {
                h.set("userIdentifier", userAuthDTO.getIdentifier().toString());
                if(userAuthDTO.getUserType() == UserType.ADMIN && headerAccountIdentifier.isPresent()) {
                    h.set("accountIdentifier", headerAccountIdentifier.get());
                }else{
                    h.set("accountIdentifier", userAuthDTO.getAccountIdentifier().toString());
                }
            }).build();

            log.info("Acesso permitido - Encaminhando para o serviço");
            return chain.filter(exchange);
        } catch (FeignException e) {
            return handleFeignException(exchange, e);
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }
    }

    private Mono<Void> handleFeignException(ServerWebExchange exchange, FeignException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.status());

        // Obtém o corpo da resposta JSON retornado pelo Feign Client
        String responseBody = ex.contentUTF8();

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }

    private boolean isPublicRoute(String path, HttpMethod method) {
        return PUBLIC_ROUTES.getOrDefault(path, Set.of()).contains(method);
    }

    /**
     * Verifica se a rota segue o formato "/v1/{qualquer_coisa}/{UUID}" e é um GET permitido.
     */
    private boolean isPublicRouteWithUUID(String path, HttpMethod method) {
        String[] parts = path.split("/");

        List<String> routesWithUUID = List.of("order", "menu", "customer", "category", "product");
        // Verifica se a URL tem 3 partes: "/v1/{prefixo}/{UUID}"
        if (parts.length == 4 && parts[1].equals("v1") && UUID_PATTERN.matcher(parts[3]).matches() && routesWithUUID.contains(parts[2])) {
            return method == HttpMethod.GET;
        }
        if (parts.length == 5 && "v1".equals(parts[1]) && "customer".equals(parts[2]) && "phone".equals(parts[3])) {
            return PHONE_PATTERN.matcher(parts[4]).matches() && HttpMethod.GET.equals(method);
        }

        return false;
    }
}
