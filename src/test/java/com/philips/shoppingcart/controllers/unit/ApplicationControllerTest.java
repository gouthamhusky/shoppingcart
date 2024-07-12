package com.philips.shoppingcart.controllers.unit;

import com.philips.shoppingcart.controllers.ApplicationController;
import com.philips.shoppingcart.pojos.Cart;
import com.philips.shoppingcart.pojos.Item;
import com.philips.shoppingcart.pojos.User;
import com.philips.shoppingcart.services.ItemService;
import com.philips.shoppingcart.services.RedisService;
import com.philips.shoppingcart.services.UserService;
import com.philips.shoppingcart.utils.GenericUtils;
import com.philips.shoppingcart.utils.SchemaValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private SchemaValidator schemaValidator;

    @Mock
    private RedisService redisService;

    @Mock
    private GenericUtils utils;

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
        Assertions.assertEquals(HttpStatus.NOT_MODIFIED, response.getStatusCode(), "Status code does not match");
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
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code does not match");
    }

    @Test
    public void whenNewItemInPostBodyExists_ShouldReturn200(){
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("username");

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

        when(itemService.getByNameAndCart("item", 1))
                .thenReturn(Optional.empty());
        when(userService.getByUsername(anyString())).
                thenReturn(Optional.of(user));
        when(utils.hashString(any())).thenReturn("hash");

        ResponseEntity<Object> response = applicationController.addToCart(auth, requestBody);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void whenNewItemInPostBodyDoesNotExists_ShouldReturn200(){
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("username");

        User user = new User(1, "username", "password", new Cart());
        String requestBody= "{\n" +
                "   \"Name\":\"PS5\",\n" +
                "   \"Quantity\":1,\n" +
                "   \"Price\":499.99\n" +
                "}";

        Item itemToUpdate = mock(Item.class);

        Mockito.doNothing().when(schemaValidator).validate(requestBody);
        Mockito.doNothing().when(redisService).save(any(), any());
        Mockito.doNothing().when(itemService).createOrUpdate(itemToUpdate);

        when(itemService.getByNameAndCart("item", 1))
                .thenReturn(Optional.empty());
        when(userService.getByUsername(anyString())).
                thenReturn(Optional.of(user));
        when(utils.hashString(any())).thenReturn("hash");

        ResponseEntity<Object> response = applicationController.addToCart(auth, requestBody);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
