package com.philips.shoppingcart.exceptions;

/**
 * Exception that results from erroneous results from the PostgresSQL database
 */
public class DBInternalException extends RuntimeException {
    public DBInternalException(String message, Exception e) {
        super(message, e);
    }
}
