package org.ecommerce.userapi.security.custom;

import java.io.IOException;

import org.ecommerce.userapi.exception.UserErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint , ResponseConfigurer{
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        responseSetting(response,UserErrorCode.AUTHENTICATION_FAILED);
    }
}