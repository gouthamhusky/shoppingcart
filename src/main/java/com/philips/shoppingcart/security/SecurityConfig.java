package com.philips.shoppingcart.security;

import com.philips.shoppingcart.controllers.ApplicationController;
import com.philips.shoppingcart.services.JDBCUserDetailsService;
import com.philips.shoppingcart.utils.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

/**
 * Security configuration class
 */
@Configuration
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JDBCUserDetailsService jdbcUserDetailsService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired CustomAuthenticationEntryPoint authenticationEntryPoint;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

    /**
     * Bean to authenticate users using the custom JDBCUserDetailsService and BCryptPasswordEncoder
     * @return BCryptPasswordEncoder
     */
    @Bean
    protected DaoAuthenticationProvider DAOauthProvider(){
        LOGGER.debug("Creating custom DaoAuthenticationProvider bean");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(jdbcUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Bean to create a SecurityFilterChain
     * @param httpSecurity The HttpSecurity object
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(configurer ->
                configurer.authenticationEntryPoint(authenticationEntryPoint)
            )
            .authorizeHttpRequests((request) ->
                request.anyRequest().authenticated()
            ).httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
    }

    /**
     * Bean to setup a custom UserDetailsManager with custom queries to fetch user details
     * @param dataSource The DataSource object
     * @return UserDetailsManager
     */

    @Bean
    public UserDetailsManager users(DataSource dataSource){
        LOGGER.debug("Creating custom UserDetailsManager bean");
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.setUsersByUsernameQuery("select username, password, 1 as enabled from user_table where username = ?");
        userDetailsManager.setAuthoritiesByUsernameQuery("select username, 'ROLE_USER' from user_table where username = ?");
        return userDetailsManager;
    }

}