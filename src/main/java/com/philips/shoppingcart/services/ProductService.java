package com.philips.shoppingcart.services;

import com.philips.shoppingcart.exceptions.DBInternalException;
import com.philips.shoppingcart.exceptions.ProductNotFoundException;
import com.philips.shoppingcart.pojos.Item;
import com.philips.shoppingcart.pojos.Product;
import com.philips.shoppingcart.repositories.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    public Optional<Product> getByName(String name) {
        try {
            LOGGER.info("Getting item by name: " + name + " from DB");
            Product result = entityManager.createQuery("SELECT p FROM Product p WHERE p.name = :name", Product.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            if (e instanceof NoResultException) {
                LOGGER.info("No product found with name: " + name);
                throw new ProductNotFoundException("Product with name " + name + " does not exist", e);
            }
            if (e instanceof NonUniqueResultException){
                LOGGER.error("Multiple products returned, which is not expected");
                throw new DBInternalException("Error occurred while querying the database", e);
            }
        }
        return Optional.empty();
    }

    public void createOrUpdate(Product product){
        LOGGER.info("Saving product with name: " + product.getName());
        productRepository.save(product);
    }

}
