package com.philips.shoppingcart.utils;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Configuration class to create a BCryptPasswordEncoder bean
 */
@Configuration
public class PasswordEncoder {

    /**
     * Method to create a BCryptPasswordEncoder bean
     * @return BCryptPasswordEncoder
     */
    @Bean
    @Scope("singleton")
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

 }
