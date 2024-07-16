package com.philips.shoppingcart.advices;

import com.philips.shoppingcart.exceptions.BootstrapParseException;
import com.philips.shoppingcart.exceptions.DBInternalException;
import com.philips.shoppingcart.exceptions.ProductNotFoundException;
import com.philips.shoppingcart.exceptions.SchemaValidationException;
import com.philips.shoppingcart.pojos.ResponseSchema;
import com.philips.shoppingcart.utils.GenericUtils;
import com.philips.shoppingcart.utils.MetricsReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler to handle exceptions thrown by the application
 */

@ControllerAdvice
public class APIExceptionHandler {

    @Autowired
    private GenericUtils utils;

    @Autowired
    private MetricsReporter metricsReporter;

    @ExceptionHandler(value = SchemaValidationException.class)
    public ResponseEntity<ResponseSchema> handleSchemaValidationException(SchemaValidationException ex) {
        metricsReporter.recordCounter(HttpStatus.BAD_REQUEST);
        ResponseSchema errorResponse = new ResponseSchema(
                ex.getMessage(),
                400,
                utils.getCurrentTime()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseSchema> handleMissingRequestBodyException(){
        metricsReporter.recordCounter(HttpStatus.BAD_REQUEST);
        ResponseSchema errorResponse = new ResponseSchema(
                "missing request payload",
                400,
                utils.getCurrentTime()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(value = BootstrapParseException.class)
    public ResponseEntity<ResponseSchema> handleBootstrapException(){
        metricsReporter.recordCounter(HttpStatus.INTERNAL_SERVER_ERROR);
        ResponseSchema errorResponse = new ResponseSchema(
                "Failure occurred in DB bootstrapping",
                500,
                utils.getCurrentTime()
        );
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(value = DBInternalException.class)
    public ResponseEntity<ResponseSchema> handleDatabaseException(){
        metricsReporter.recordCounter(HttpStatus.INTERNAL_SERVER_ERROR);
        ResponseSchema errorResponse = new ResponseSchema(
                "Failure occurred while querying the database",
                500,
                utils.getCurrentTime()
        );
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(value = ProductNotFoundException.class)
    public ResponseEntity<ResponseSchema> productNotFoundException(ProductNotFoundException ex) {
        metricsReporter.recordCounter(HttpStatus.BAD_REQUEST);
        ResponseSchema errorResponse = new ResponseSchema(
                ex.getMessage(),
                400,
                utils.getCurrentTime()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
