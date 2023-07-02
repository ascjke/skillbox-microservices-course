package ru.borisov.users.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) throws IOException {

        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(401);
        res.getWriter();
        res.getWriter().write("401. Требуется аутентификация");
    }
}
