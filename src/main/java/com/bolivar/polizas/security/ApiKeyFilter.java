package com.bolivar.polizas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "x-api-key";

    @Value("${api.security.key}")
    private String apiKeyEsperada;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // La consola de H2 queda fuera del filtro (solo para pruebas locales)
        if (uri.startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKeyRecibida = request.getHeader(HEADER_NAME);

        if (apiKeyRecibida == null || !apiKeyRecibida.equals(apiKeyEsperada)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Header x-api-key inválido o ausente\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
