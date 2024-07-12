package com.philips.shoppingcart.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Entity class for the cart table
 */
@Entity(name = "cart")
@Data
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;

    @JsonProperty("Items")
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<Item> items;

    public void addItem(Item item) {
        if (items == null) {
            items = List.of(item);
            return;
        }
        items.add(item);
    }
}
