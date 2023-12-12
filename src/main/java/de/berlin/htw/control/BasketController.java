package de.berlin.htw.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.berlin.htw.boundary.dto.Item;
import de.berlin.htw.boundary.dto.Order;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotSupportedException;

import de.berlin.htw.boundary.dto.Basket;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.quarkus.redis.datasource.list.ListCommands;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@Dependent
public class BasketController {

    @Inject
    protected RedisDataSource redisDS;
    
    protected ValueCommands<String, Integer> countCommands;
    protected ListCommands<String, String> stringListCommands;

    @Inject
    ObjectMapper objectMapper;

    @PostConstruct
    protected void init() {
        countCommands = redisDS.value(Integer.class);
        stringListCommands = redisDS.list(String.class);
    }

    public Basket getBasket(Principal userPrincipal) {
        String key = getPrincipalBasketKey(userPrincipal);
        List<String> jsonItems = stringListCommands.lrange(key, 0, -1);
        List<Item> items = new ArrayList<>();

        for (String jsonItem : jsonItems) {
            Item item = null;
            try {
                item = objectMapper.readValue(jsonItem, Item.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            items.add(item);
        }

        var basket = new Basket();
        basket.setItems(items);
        return basket;
    }

    public Order checkout(Principal userPrincipal) {
        String key = getPrincipalBasketKey(userPrincipal);
        Basket basket = getBasket(userPrincipal);
        // Logic to checkout the basket, typically involving:
        // - Retrieving all items
        // - Processing payment
        // - Creating an order
        // - Clearing the basket in Redis
        // ...
        redisDS.key().del(key); // Clear the basket after checkout
        // Construct the order object and location URI
        Order order = new Order(); // Populate order details
        order.setItems(basket.getItems());
        return order; // Return the order object
    }


    public void clearBasket(Principal userPrincipal) {
        String key = getPrincipalBasketKey(userPrincipal);
        redisDS.key().del(key);
    }

    public Basket addItem(Principal userPrincipal, String productId, Item item) {
        String key = getPrincipalBasketKey(userPrincipal);
        // Serialize item to JSON
        String jsonItem = null;
        try {
            jsonItem = objectMapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // Push to the list
        stringListCommands.rpush(key, jsonItem);
        redisDS.key().expire(key, 120);
        // Return updated basket
        return getBasket(userPrincipal);
    }

    public Basket removeItem(Principal userPrincipal, String productId) {
        String key = getPrincipalBasketKey(userPrincipal);
        // Find the index of the item with the given product ID
        // Note: This requires each item JSON to have the product ID as a property
        List<String> items = stringListCommands.lrange(key, 0, -1);
        int indexToRemove = -1;
        for (int i = 0; i < items.size(); i++) {
            Item item = null;
            try {
                item = objectMapper.readValue(items.get(i), Item.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if (productId.equals(item.getProductId())) {
                indexToRemove = i;
                break;
            }
        }
        if (indexToRemove != -1) {
            // Remove the item at the found index
            String itemToRemove = items.get(indexToRemove);
            stringListCommands.lrem(key, 1, itemToRemove);
        }
        redisDS.key().expire(key, 120);
        // Return updated basket
        return getBasket(userPrincipal);
    }


    public Basket changeItemCount(Principal userPrincipal, String productId, Item updatedItem) {
        String key = getPrincipalBasketKey(userPrincipal);
        // Serialize item to JSON
        String jsonItem = null;
        try {
            jsonItem = objectMapper.writeValueAsString(updatedItem);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // Get all items
        List<String> jsonItems = stringListCommands.lrange(key, 0, -1);
        // Find the item to update
        for (int i = 0; i < jsonItems.size(); i++) {
            String jsonItemToCheck = jsonItems.get(i);
            Item itemToCheck = null;
            try {
                itemToCheck = objectMapper.readValue(jsonItemToCheck, Item.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if (itemToCheck.getProductId().equals(productId)) {
                // Update the item
                jsonItems.set(i, jsonItem);
                break;
            }
        }
        // Clear the basket
        redisDS.key().del(key);
        // Push all items back to the list
        for (String jsonItemToPush : jsonItems) {
            stringListCommands.rpush(key, jsonItemToPush);
        }
        redisDS.key().expire(key, 120);
        return getBasket(userPrincipal);
    }

    private String getPrincipalBasketKey(Principal userPrincipal) {
        return String.format("basket:%s", userPrincipal.getName());
    }
}
