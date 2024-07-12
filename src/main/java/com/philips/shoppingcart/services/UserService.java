package com.philips.shoppingcart.services;


import com.philips.shoppingcart.pojos.User;
import com.philips.shoppingcart.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for the User entity
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    /**
     * Method to get a user by their username
     * @param username The username of the user
     * @return The user object
     */
    public Optional<User> getByUsername(String username) {
        LOGGER.info("Getting user by username: " + username + " from DB");
        return Optional.of(entityManager.createQuery("SELECT u FROM user_table u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult());
    }

    /**
     * Method to create or update a user in the database
     * @param user The user object
     */
    @Transactional
    public void createOrUpdate(User user) {
        LOGGER.info("Creating or updating user: " + user.getUsername() + " in DB");
        userRepository.save(user);
    }
}
