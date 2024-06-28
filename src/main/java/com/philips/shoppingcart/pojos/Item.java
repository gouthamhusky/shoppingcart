package com.philips.shoppingcart.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

/**
    * Entity class for the item table
 */
@Entity(name = "item")
@Data
@Schema(description = "Represents an item in a cart")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Quantity")
    private int quantity;

    @JsonProperty("Price")
    private double price;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Cart cart;
}
