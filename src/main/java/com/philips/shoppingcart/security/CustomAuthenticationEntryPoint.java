package com.philips.shoppingcart.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.philips.shoppingcart.controllers.ApplicationController;
import com.philips.shoppingcart.pojos.ResponseSchema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.lang.String.valueOf;

/**
 * Authentication entry point to handle unauthorized access with a custom response
 */

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    /**
     * Method to intercepts unauthorized access requests and sends a response to the client
     * @param request The HTTP request
     * @param response The HTTP response
     * @param authException The authentication exception
     * @throws IOException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        LOGGER.error("Unauthorized access: " + authException.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        ResponseSchema errorResponse = new ResponseSchema("login credentials not provided or is invalid", 401, valueOf(System.currentTimeMillis()));
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(errorResponse));
    }
}
