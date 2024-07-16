package com.philips.shoppingcart.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.philips.shoppingcart.pojos.*;
import com.philips.shoppingcart.services.ItemService;
import com.philips.shoppingcart.services.ProductService;
import com.philips.shoppingcart.services.RedisService;
import com.philips.shoppingcart.services.UserService;
import com.philips.shoppingcart.utils.GenericUtils;
import com.philips.shoppingcart.utils.MetricsReporter;
import com.philips.shoppingcart.utils.SchemaValidator;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller that handles all the requests related to the cart API
 */

@RestController
@RequestMapping("/api/v1/cart")
public class ApplicationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SchemaValidator schemaValidator;

    @Autowired
    private GenericUtils genericUtils;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MetricsReporter metricsReporter;

    @Value("${cart.webapp.redis.key}")
    private String redisHash;

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
        LOGGER.debug("User: + " + authentication.getName() + " authenticated successfully");

        User user = userService.getByUsername(authentication.getName()).get();
        String hashFromRedis = redisService.findInHash(redisHash, String.valueOf(user.getId()));

        if (hashFromRedis != null && hashFromRedis.equals(eTag)) {
            LOGGER.info(user.getUsername() + "'s cart not modified since last fetch");
            metricsReporter.recordCounter(HttpStatus.NOT_MODIFIED);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(hashFromRedis).build();
        }

        Cart cart = user.getCart();
        String hash = genericUtils.hashString(cart);

        LOGGER.info("Updated cart fetched successfully for " + user.getUsername());
        metricsReporter.recordCounter(HttpStatus.NOT_MODIFIED);
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
        LOGGER.debug("User : + " + authentication.getName() + " authenticated successfully");

        schemaValidator.validate(requestBody);
        LOGGER.debug("Schema validation successful");

        User user = userService.getByUsername(authentication.getName()).get();
        ItemDTO itemDTO;
        itemDTO = OBJECT_MAPPER.readValue(requestBody, ItemDTO.class);

        Cart userCart = user.getCart();
        Optional<Item> itemInDb = itemService.getByProductAndCart(itemDTO.getName(), userCart.getId());

        if (itemInDb.isEmpty()){
            Item item = new Item();
            item.setQuantity(itemDTO.getQuantity());
            Optional<Product> product = productService.getByName(itemDTO.getName());
            item.setProduct(product.get());
            item.setCart(userCart);
            itemService.createOrUpdate(item);
            LOGGER.info("New item: " + itemDTO.getName() + " added to cart successfully");

            LOGGER.info("Computing latest hash for user cart: " + user.getUsername());
            String hash = genericUtils.hashString(userCart);
            redisService.save(redisHash, new HashMap<>(Map.of(String.valueOf(user.getId()), hash)));

            return ResponseEntity.ok().eTag(hash).body(new ResponseSchema("item added to cart successfully", 200, genericUtils.getCurrentTime()));

        }
        else {
            LOGGER.info("Item to add already exists...");
            ResponseSchema responseSchema = new ResponseSchema(
                    "Item already exists in the cart",
                    400,
                    genericUtils.getCurrentTime()
            );
            return ResponseEntity.badRequest().body(responseSchema);
        }
    }
}
