package com.philips.shoppingcart.advices;

import com.philips.shoppingcart.exceptions.SchemaValidationException;
import com.philips.shoppingcart.pojos.ResponseSchema;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler to handle exceptions thrown by the application
 */

@ControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(value = SchemaValidationException.class)
    public ResponseEntity<Object> handleSchemaValidationException(SchemaValidationException ex) {
        ResponseSchema errorResponse = new ResponseSchema(ex.getMessage(), 400, String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleMissingRequestBodyException(){
        ResponseSchema errorResponse = new ResponseSchema("missing request payload", 400, String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
