package com.philips.shoppingcart.controllers.unit;

import com.philips.shoppingcart.controllers.ApplicationController;
import com.philips.shoppingcart.exceptions.SchemaValidationException;
import com.philips.shoppingcart.pojos.Cart;
import com.philips.shoppingcart.pojos.Item;
import com.philips.shoppingcart.pojos.Product;
import com.philips.shoppingcart.pojos.User;
import com.philips.shoppingcart.services.ItemService;
import com.philips.shoppingcart.services.ProductService;
import com.philips.shoppingcart.services.RedisService;
import com.philips.shoppingcart.services.UserService;
import com.philips.shoppingcart.utils.GenericUtils;
import com.philips.shoppingcart.utils.MetricsReporter;
import com.philips.shoppingcart.utils.SchemaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApplicationControllerTest {

    @InjectMocks
    @Spy
    private ApplicationController applicationController;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private ProductService productService;

    @Mock
    private SchemaValidator schemaValidator;

    @Mock
    private RedisService redisService;

    @Mock
    private GenericUtils utils;

    @Mock
    private MetricsReporter metricsReporter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenEtagMatches_shouldReturn304(){
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("username");

        User user = new User(1, "username", "password", null);
        when(userService.getByUsername(anyString())).thenReturn(Optional.of(user));
        when(redisService.findInHash(any(), anyString())).thenReturn("eTag");

        ResponseEntity<Object> response = applicationController.getCart(auth, "eTag");
        assertEquals(HttpStatus.NOT_MODIFIED, response.getStatusCode(), "Status code does not match");
    }

    @Test
    public void whenEtagDoesNotMatches_shouldReturn200(){
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("username");

        User user = new User(1, "username", "password", null);
        when(userService.getByUsername(anyString())).thenReturn(Optional.of(user));
        when(redisService.findInHash("hashes", String.valueOf(1))).thenReturn("differentETag");
        when(utils.hashString(any(Cart.class))).thenReturn("hash");

        ResponseEntity<Object> response = applicationController.getCart(auth, "eTag");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code does not match");
    }

    @Test
    public void whenNewItemInPostBodyExists_ShouldReturn400(){
        Authentication auth = mock(Authentication.class);
        Product product = mock(Product.class);
        when(auth.getName()).thenReturn("username");

        Item item = mock(Item.class);
        item.setProduct(product);

        User user = new User(1, "username", "password", new Cart());
        String requestBody= """
                {
                   "Name":"PS5",
                   "Quantity":1,
                   "Price":499.99
                }
                """;

        Mockito.doNothing().when(schemaValidator).validate(requestBody);
        Mockito.doNothing().when(redisService).save(any(), any());

        when(itemService.getByProductAndCart(anyString(), anyInt()))
                .thenReturn(Optional.of(item));
        when(userService.getByUsername(anyString())).
                thenReturn(Optional.of(user));
        when(productService.getByName(anyString())).
                thenReturn(Optional.of(product));
        when(utils.hashString(any())).thenReturn("hash");

        ResponseEntity<Object> response = applicationController.addToCart(auth, requestBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void whenNewItemInPostBodyDoesNotExists_ShouldReturn200(){
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("username");

        Product product = mock(Product.class);

        User user = new User(1, "username", "password", new Cart());
        String requestBody= """
                {
                   "Name":"PS5",
                   "Quantity":1,
                   "Price":499.99
                }""";

        Item itemToUpdate = mock(Item.class);

        Mockito.doNothing().when(schemaValidator).validate(requestBody);
        Mockito.doNothing().when(redisService).save(any(), any());
        Mockito.doNothing().when(itemService).createOrUpdate(itemToUpdate);

        when(itemService.getByProductAndCart(anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(userService.getByUsername(anyString())).
                thenReturn(Optional.of(user));
        when(productService.getByName(anyString())).
                thenReturn(Optional.of(product));
        when(utils.hashString(any())).thenReturn("hash");

        ResponseEntity<Object> response = applicationController.addToCart(auth, requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void whenInvalidPostBody_ShouldReturn400(){
        Authentication auth = mock(Authentication.class);
        User user = new User(1, "username", "password", new Cart());

        String body= """
                {
                   "Quantity":1,
                   "Price":499.99
                }""";

        Mockito.doThrow(SchemaValidationException.class)
                .when(schemaValidator).validate(body);

        assertThrows(
                SchemaValidationException.class,
                () -> applicationController.addToCart(auth, body)
        );
    }
}
