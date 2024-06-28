package com.philips.shoppingcart.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * Response schema for the API responses
 */
@Data
@AllArgsConstructor
public class ResponseSchema {

    @JsonProperty
    private String message;

    @JsonProperty
    private int statusCode;

    @JsonProperty
    private String timestamp;
}
