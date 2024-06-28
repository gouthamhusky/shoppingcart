package com.philips.shoppingcart.repositories;

import com.philips.shoppingcart.pojos.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Integer> {
}
