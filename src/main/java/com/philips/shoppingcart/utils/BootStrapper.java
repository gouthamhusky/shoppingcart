package com.philips.shoppingcart.utils;

import com.philips.shoppingcart.pojos.Cart;
import com.philips.shoppingcart.pojos.User;
import com.philips.shoppingcart.services.UserService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


/**
 * Class to bootstrap the PostgresSQL DB with users
 */
@Component
public class BootStrapper implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Method to read the users.csv file and create users in the DB on application startup
     * @param event The ApplicationReadyEvent
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    new ClassPathResource("users.csv").getInputStream(), StandardCharsets.UTF_8
                )
            )
        ) {
            CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
            for (CSVRecord record : parser) {
                String username = record.get(0);
                String password = record.get(1);

                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password));
                user.setCart(new Cart());
                userService.createOrUpdateUser(user);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
