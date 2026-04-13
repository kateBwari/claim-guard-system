package org.kate._policyservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.kate._policyservice.model.ApiResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

@Override
public void commence(HttpServletRequest request, HttpServletResponse response,
                     AuthenticationException authException) throws IOException, ServletException {

    // Set headers
    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    // Create the response object
    ApiResponse apiResponse = new ApiResponse(
            false,
            "Unauthorized: You need a valid token to access this resource.",
            null
    );

    // Convert to JSON
    ObjectMapper mapper = new ObjectMapper();
    String jsonResponse = mapper.writeValueAsString(apiResponse);

    // Write to the response - No try-catch needed because of 'throws' above
    response.getWriter().write(jsonResponse);
}}