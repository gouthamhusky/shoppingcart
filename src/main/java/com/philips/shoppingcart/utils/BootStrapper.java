package com.philips.shoppingcart.utils;

import com.philips.shoppingcart.exceptions.BootstrapParseException;
import com.philips.shoppingcart.pojos.Cart;
import com.philips.shoppingcart.pojos.Product;
import com.philips.shoppingcart.pojos.User;
import com.philips.shoppingcart.services.ProductService;
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
import java.util.function.Consumer;


/**
 * Class to bootstrap the PostgresSQL DB with users
 */
@Component
public class BootStrapper implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ProductService productService;

    /**
     *
     * Method to read the users.csv file and create users in the DB on application startup
     * @param event The ApplicationReadyEvent
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        processCsvFile("users.csv", this::processUserRecord);
        processCsvFile("inventory.csv", this::processProductRecord);
    }

    private void processCsvFile(String filePath, Consumer<CSVRecord> recordProcessor) {
        try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new ClassPathResource(filePath).getInputStream(), StandardCharsets.UTF_8
                    )
                );
                CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT)
        ) {
            parser.forEach(recordProcessor);
        } catch (Exception e) {
            throw new BootstrapParseException("Error while parsing " + filePath, e);
        }
    }

    private void processUserRecord(CSVRecord record) {
        String username = record.get(0);
        String password = record.get(1);

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setCart(new Cart());
        userService.createOrUpdate(user);
    }

    private void processProductRecord(CSVRecord record) {
        String name = record.get(0);
        String price = record.get(1);

        Product product = new Product();
        product.setName(name);
        product.setPrice(Double.valueOf(price));
        productService.createOrUpdate(product);
    }

}
