package com.philips.shoppingcart.utils;

import com.philips.shoppingcart.exceptions.SchemaValidationException;
import jakarta.annotation.PostConstruct;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class to validate the request body against a JSON schema
 */
@Component
public class SchemaValidator {

    private static final String SCHEMA = "/ItemSchema.json";

    private Schema postSchema;

    @PostConstruct
    private void setupSchemaProcessing() throws IOException {
        InputStream inputStream = SchemaValidator.class.getResourceAsStream(SCHEMA);
        assert inputStream != null;

        JSONObject schemaObject = new JSONObject(new JSONTokener(inputStream));
        postSchema = SchemaLoader.load(schemaObject);
        inputStream.close();
    }

    /**
     * Method to validate the request body against a JSON schema
     * @param requestBody The request body
     */
    public void validate(String requestBody){
        try {
            JSONObject itemJSON = new JSONObject(requestBody);
            postSchema.validate(itemJSON);
        } catch (ValidationException e) {
            throw new SchemaValidationException("Invalid request body", e);
        }
    }
}
