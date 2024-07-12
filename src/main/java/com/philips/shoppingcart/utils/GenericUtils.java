package com.philips.shoppingcart.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.philips.shoppingcart.pojos.Cart;
import com.philips.shoppingcart.pojos.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generic methods
 */
@Component
public class GenericUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${cart.webapp.datetime.format}")
    private String dateTimeFormat;

    /**
     * Method to hash a string using MD5
     * @param cart The cart object
     * @return The hashed string
     */
    public String hashString(Cart cart) {
        try {
            String cartJSON = OBJECT_MAPPER.writeValueAsString(cart);

            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(cartJSON.getBytes(StandardCharsets.UTF_8));
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashBuilder = new StringBuilder(no.toString(16));

            while (hashBuilder.length() < 32) {
                hashBuilder.insert(0, "0");
            }

            return hashBuilder.toString();
        }
        catch (NoSuchAlgorithmException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCurrentTime(){
        long currentTimeMillis = System.currentTimeMillis();

        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTimeMillis), ZoneId.systemDefault());
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        return zonedDateTime.format(formatter);
    }

}
