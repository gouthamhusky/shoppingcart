package com.philips.shoppingcart.exceptions;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String message, Exception e) {
        super(message, e);
    }
}
