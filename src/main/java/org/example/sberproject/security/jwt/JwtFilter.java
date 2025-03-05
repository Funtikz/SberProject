package org.example.sberproject.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.sberproject.security.CustomUserDetails;
import org.example.sberproject.security.CustomUserService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private CustomUserService userDetailsService; // Сервис для получения пользователя из БД

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 1. Извлечение JWT токена из куки
        String jwtToken = extractJwtFromCookies(request);
        // 2. Проверка, есть ли токен и не авторизован ли уже пользователь
        if (jwtToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 3. Валидация токена
            if (jwtService.validateJwtToken(jwtToken)) {
                // 4. Извлечение email (или username) из токена
                String number = jwtService.getEmailFromToken(jwtToken);

                // 5. Загрузка пользователя из базы данных
                CustomUserDetails customUserDetails = userDetailsService.loadUserByUsername(number);

                // 6. Создание объекта для Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                customUserDetails, null, customUserDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 7. Установка авторизации в SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 8. Продолжение цепочки фильтров
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromCookies(HttpServletRequest request) {
        // Получение всех куки
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // Поиск куки с названием JWT
            return Arrays.stream(cookies)
                    .filter(cookie -> "jwt-auth-token".equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }
        return null;
    }

}
