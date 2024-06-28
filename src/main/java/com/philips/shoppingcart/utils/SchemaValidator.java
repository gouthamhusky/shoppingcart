package com.philips.shoppingcart.utils;

import com.philips.shoppingcart.exceptions.SchemaValidationException;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Class to validate the request body against a JSON schema
 */
@Component
public class SchemaValidator {

    private static final String SCHEMA = "/ItemSchema.json";

    /**
     * Method to validate the request body against a JSON schema
     * @param requestBody The request body
     */
    public void validate(String requestBody) {
        try {
            JSONObject itemJSON = new JSONObject(requestBody);
            InputStream inputStream = getClass().getResourceAsStream(SCHEMA);
            assert inputStream != null;
            JSONObject schemaObject = new JSONObject(new JSONTokener(inputStream));

            Schema schema = SchemaLoader.load(schemaObject);
            schema.validate(itemJSON);

        } catch (ValidationException e) {
            throw new SchemaValidationException("Invalid request body", e);
        }
    }
}
