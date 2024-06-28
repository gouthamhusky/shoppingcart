package com.philips.shoppingcart.services;

import com.philips.shoppingcart.pojos.User;
import com.philips.shoppingcart.security.JDBCUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for the User entity
 */
@Service
public class JDBCUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(JDBCUserDetailsService.class);

    /**
     * Method to load a user by their username
     * @param username The username of the user
     * @return The UserDetails object
     * @throws UsernameNotFoundException If the username is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("Loading user by username: " + username);
        Optional<User> user = userService.getUserByUsername(username);
        user.orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
        return user.map(JDBCUserDetails::new).get();
    }
}
