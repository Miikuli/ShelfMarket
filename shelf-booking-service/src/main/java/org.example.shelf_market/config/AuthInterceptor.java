package org.example.shelf_market.config;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Получаем username, role, userId из заголовков, добавленных API Gateway
        String username = request.getHeader("X-Username");
        String role = request.getHeader("X-Role");
        String userId = request.getHeader("X-UserId");

        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // Добавляем в атрибуты запроса для использования в контроллерах
        request.setAttribute("username", username);
        request.setAttribute("role", role != null ? role : "USER");
        request.setAttribute("userId", userId);
        logger.info("Request attributes set: username={}, role={}, userId={}", username, role, userId);
        return true;
    }
}