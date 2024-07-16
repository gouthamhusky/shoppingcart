package com.philips.shoppingcart.services;

import com.philips.shoppingcart.exceptions.DBInternalException;
import com.philips.shoppingcart.pojos.Item;
import com.philips.shoppingcart.repositories.ItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for the Item entity
 */
@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemService.class);

    /**
     * Method to create or update an item in the database
     * @param item The item object
     */
    @Transactional
    public void createOrUpdate(Item item) {
        LOGGER.info("Creating or updating item: " + item.getProduct().getName() + " in DB");
        itemRepository.save(item);
    }

    public Optional<Item> getByProductAndCart(String productName, int cartID){
        try {
            Item result = entityManager.createQuery(
            "SELECT i from item i where i.product.name = :productName and i.cart.id = :cartID", Item.class
                )
                .setParameter("productName", productName)
                .setParameter("cartID", cartID).getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            if (e instanceof NoResultException){
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public void delete(Item item){
        itemRepository.delete(item);
    }
}
