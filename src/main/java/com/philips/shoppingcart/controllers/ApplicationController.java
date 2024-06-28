package com.philips.shoppingcart.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.philips.shoppingcart.pojos.Cart;
import com.philips.shoppingcart.pojos.Item;
import com.philips.shoppingcart.pojos.ResponseSchema;
import com.philips.shoppingcart.pojos.User;
import com.philips.shoppingcart.services.ItemService;
import com.philips.shoppingcart.services.UserService;
import com.philips.shoppingcart.utils.GenericUtils;
import com.philips.shoppingcart.utils.SchemaValidator;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller that handles all the requests related to the cart API
 */

@RestController
@RequestMapping("/api/v1/cart")
@CrossOrigin(value="*")
public class ApplicationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private SchemaValidator schemaValidator;

    @Autowired
    private GenericUtils genericUtils;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

    /**
     * Fetches the cart for an authenticated user
     * @param authentication The authentication object
     * @param eTag The eTag value
     * @return  Deserialized cart object in JSON format
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<Object> getCart(Authentication authentication, @Nullable @RequestHeader("If-None-Match") String eTag) {
        LOGGER.debug("User : + " + authentication.getName() + " authenticated successfully");
        User user = userService.getUserByUsername(authentication.getName()).get();
        Cart cart = user.getCart();
        String hash = genericUtils.hashString(cart);
        if (hash.equals(eTag)) {
            LOGGER.info(user.getUsername() + "'s cart not modified since last fetch");
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(hash).build();
        }
        LOGGER.info("Updated cart fetched successfully for " + user.getUsername());
        return ResponseEntity.ok().eTag(hash).body(cart);
    }

    /**
     * Adds an item to the cart of an authenticated user
     * @param authentication The authentication object
     * @param requestBody The request body
     * @return  Response entity with the status of the operation
     */

    @SneakyThrows
    @PostMapping(produces = "application/json")
    public ResponseEntity<Object> addToCart(Authentication authentication, @RequestBody String requestBody) {
        if (authentication == null) {
            return ResponseEntity.badRequest().build();
        }

        schemaValidator.validate(requestBody);
        LOGGER.debug("Schema validation successful");
        User user = userService.getUserByUsername(authentication.getName()).get();
        Item item;
        item = OBJECT_MAPPER.readValue(requestBody, Item.class);

        Optional<Item> itemInDb = itemService.getItemByName(item.getName());
        if (itemInDb.isEmpty()) {
            item.setCart(user.getCart());
            itemService.createOrUpdateItem(item);
            user.getCart().addItem(item);
            LOGGER.info("New item: " + item.getName() + " added to cart successfully");
        } else {
            Item itemToUpdate = itemInDb.get();
            itemToUpdate.setQuantity(itemToUpdate.getQuantity() + item.getQuantity());
            itemService.createOrUpdateItem(itemToUpdate);
            LOGGER.info("Item: " + item.getName() + " updated in cart successfully");
        }

        return ResponseEntity.ok(new ResponseSchema("item added to cart successfully", 200, String.valueOf(System.currentTimeMillis())));
    }

}
