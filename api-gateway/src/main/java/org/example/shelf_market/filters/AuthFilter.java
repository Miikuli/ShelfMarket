package org.example.shelf_market.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.core.ParameterizedTypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    private final WebClient.Builder webClientBuilder;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Пропускаем публичные маршруты
            String path = exchange.getRequest().getURI().getPath();
            if (path.startsWith("/api/auth/") || path.equals("/api/users/create")) {
                return chain.filter(exchange);
            }

            // Получаем токен
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Валидируем токен через user-service
            return webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8081/api/auth/validate")
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .flatMap(userData -> {
                        String username = (String) userData.get("username");
                        String role = (String) userData.get("role");
                        String userId = userData.get("userId").toString(); // UUID as string

                        logger.info("Validated user: username={}, role={}, userId={}", username, role, userId);

                        // Добавляем username, role, userId в заголовки
                        ServerHttpRequest newRequest = exchange.getRequest().mutate()
                                .header("X-Username", username)
                                .header("X-Role", role)
                                .header("X-UserId", userId)
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .build();
                        return chain.filter(exchange.mutate().request(newRequest).build());
                    })
                    .onErrorResume(e -> {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });
        };
    }

    public static class Config {
        // Конфигурация фильтра
    }
}