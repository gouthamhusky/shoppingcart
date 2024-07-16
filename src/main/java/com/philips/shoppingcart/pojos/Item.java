package com.philips.shoppingcart.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * Entity class for the item table
 */
@Entity(name = "item")
@Data
@Schema(description = "Represents an item in a cart")
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;

    @JsonProperty("Name")
    @Column(unique = true)
    private String name;

    @JsonProperty("Quantity")
    private Integer quantity;

    @JsonProperty("Price")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    private Cart cart;

    public Item(String name, Integer quantity, Double price){
        this.setName(name);
        this.setQuantity(quantity);
        this.setPrice(price);
    }

    /**
     * Convenience method to update fields of an item
     * @param name new name value
     * @param quantity new quantity value
     * @param price new price value
     */
    public void updateFields(String name, Integer quantity, Double price){
        this.setName(name);
        this.setQuantity(quantity);
        this.setPrice(price);
    }
}
