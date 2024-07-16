package com.philips.shoppingcart.repositories;

import com.philips.shoppingcart.pojos.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
