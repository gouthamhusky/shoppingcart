package com.philips.shoppingcart.exceptions;

/**
    * Custom exception to handle JSON schema validation errors
 */
public class SchemaValidationException extends RuntimeException{

    public SchemaValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
