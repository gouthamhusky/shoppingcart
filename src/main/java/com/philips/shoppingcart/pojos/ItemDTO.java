package com.philips.shoppingcart.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer Object for SERDE purposes
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Quantity")
    private Integer quantity;

    @JsonProperty("Price")
    private Double price;
}
