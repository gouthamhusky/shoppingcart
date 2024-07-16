package com.philips.shoppingcart.controllers.integration;

import com.philips.shoppingcart.ShoppingcartApplication;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = {ShoppingcartApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationControllerTest {

    private static final Properties properties = new Properties();
    private static String username;
    private static String password;

    @BeforeAll
    public static void setup() {
        try{
            InputStream input = new FileInputStream("src/test/resources/test.properties");
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        username = properties.getProperty("username");
        password = properties.getProperty("password");

        RestAssured.baseURI = "http://localhost:8080";
    }

    private static Stream<Arguments> provideHttpMethods() {
        return Stream.of(
                Arguments.of(Method.GET),
                Arguments.of(Method.POST)
        );
    }

    @ParameterizedTest
    @MethodSource("provideHttpMethods")
    @Order(1)
    public void testUnauthenticatedAccess(Method httpMethod) {
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "login credentials not provided or is invalid");
        expectedResponse.put("statusCode", "401");

        Response response = given().request(httpMethod, "/api/v1/cart");

        assertEquals(401, response.getStatusCode());
        assertEquals(expectedResponse.get("message"), response.jsonPath().get("message"));
        assertEquals(expectedResponse.get("statusCode"), response.jsonPath().get("statusCode").toString());
    }

    @Test
    @Order(2)
    public void testWithEmptyCart() {
        Response response = given().auth().preemptive().basic(username, password).get("/api/v1/cart");

        assertEquals(200, response.getStatusCode());
        assertEquals(0, response.jsonPath().getList("Items").size());
    }

    @Test
    @Order(3)
    public void testAdditionToCart() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Name", "PS5");
        requestBody.put("Quantity", 1);
        requestBody.put("Price", 499.99);

        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "item added to cart successfully");
        expectedResponse.put("statusCode", "200");


       Response response =  given().auth().preemptive().basic(username, password)
                .body(requestBody)
                .post("/api/v1/cart");

        assertEquals(200, response.getStatusCode());
        assertEquals(expectedResponse.get("message"), response.jsonPath().get("message"));
        assertEquals(expectedResponse.get("statusCode"), response.jsonPath().get("statusCode").toString());
    }

    @Test
    @Order(4)
    public void testWithNonEmptyCart() {
        Response response = given().auth().preemptive().basic(username, password).get("/api/v1/cart");

        System.out.println(response.getBody().asString());
        assertEquals(200, response.getStatusCode());
        assertEquals(1, response.jsonPath().getList("Items").size());
    }
}