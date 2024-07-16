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
        LOGGER.info("Creating or updating item: " + item.getName() + " in DB");
        itemRepository.save(item);
    }

    /**
     * Method to get an item by its name
     * @param name The name of the item
     * @return The item object
     */
    public Optional<Item> getByName(String name) {
        try {
            LOGGER.info("Getting item by name: " + name + " from DB");
            Item result = entityManager.createQuery("SELECT i FROM item i WHERE i.name = :name", Item.class)
                .setParameter("name", name)
                .getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            if (e instanceof NoResultException) {
                LOGGER.info("No item found with name: " + name);
                return Optional.empty();
            }
            if (e instanceof NonUniqueResultException){
                LOGGER.error("Multiple items returned, which is not expected");
                throw new DBInternalException("Error occurred while querying the database", e);
            }
        }
        return Optional.empty();
    }

    public Optional<Item> getByNameAndCart(String name, int cartID) {
        try {
            LOGGER.info("Getting item by name: " + name + " and cartID: " + cartID + " from DB");
            Item result = entityManager.createQuery("SELECT i from item i where i.name = :name and i.cart.id = :cartID", Item.class)
                    .setParameter("name", name)
                    .setParameter("cartID", cartID)
                    .getSingleResult();
            return Optional.of(result);
        } catch (Exception e){
            if (e instanceof NoResultException) {
                LOGGER.error("No item found with name: " + name);
                return Optional.empty();
            }
            if (e instanceof NonUniqueResultException){
                LOGGER.error("Multiple items returned, which is not expected");
                throw new DBInternalException("Error occurred while querying the database", e);
            }
        }
        return Optional.empty();
    }

    public void delete(Item item){
        itemRepository.delete(item);
    }
}
