package com.app.two.factor.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("Http Status 401 {}", authException.getMessage());

        // Configurar o cabeçalho e o corpo da resposta
        response.setHeader("www-authenticate", "Bearer realm='/api/v1/auth'");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        RuntimeException error = new RuntimeException("Error relacionado ao Token Jwt");

        String jsonResponse = objectMapper.writeValueAsString(error);

        // Escrever o corpo da resposta
        response.getWriter().write(jsonResponse);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Definir manualmente o status HTTP

        // Limpar o buffer e enviar a resposta
        response.flushBuffer();
    }
}