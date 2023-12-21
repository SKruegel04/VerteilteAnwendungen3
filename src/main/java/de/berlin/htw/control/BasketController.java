package de.berlin.htw.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.berlin.htw.boundary.dto.Item;
import de.berlin.htw.boundary.dto.Order;
import de.berlin.htw.entity.dto.UserEntity;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import de.berlin.htw.boundary.dto.Basket;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.quarkus.redis.datasource.list.ListCommands;
import jakarta.transaction.Transactional;

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

    @Inject
    OrderController orderController;

    @Inject
    UserController userController;

    @PostConstruct
    protected void init() {
        countCommands = redisDS.value(Integer.class);
        stringListCommands = redisDS.list(String.class);
    }

    public Basket getBasket(Principal userPrincipal) {
        String key = getPrincipalBasketKey(userPrincipal);
        return loadBasket(key);
    }

    public Float getBasketTotal(Principal userPrincipal) {
        String key = getPrincipalBasketKey(userPrincipal);
        Basket basket = loadBasket(key);
        return basket.getItems().stream()
            .map(item -> item.getPrice() * item.getCount())
            .reduce(0.0f, Float::sum);
    }

    @Transactional
    public Order checkout(Principal userPrincipal) {
        String key = getPrincipalBasketKey(userPrincipal);
        Basket basket = getBasket(userPrincipal);
        redisDS.key().del(key);

        Order order = new Order();
        order.setItems(basket.getItems());
        order.setTotal(
            basket.getItems().stream()
                .map(item -> item.getPrice() * item.getCount())
                .reduce(0.0f, Float::sum)
        );
        UserEntity user = (UserEntity) userPrincipal;
        userController.updateBalance(userPrincipal, user.getBalance() - order.getTotal());
        orderController.create(order);
        return order; // Return the order object
    }


    public void clearBasket(Principal userPrincipal) {
        String key = getPrincipalBasketKey(userPrincipal);
        redisDS.key().del(key);
    }

    public Basket addItem(Principal userPrincipal, String productId, Item item) {
        String key = getPrincipalBasketKey(userPrincipal);
        Basket basket = getBasket(userPrincipal);

        for (Item itemInBasket : basket.getItems()) {
            if (productId.equals(itemInBasket.getProductId())) {
                // Update the item count
                itemInBasket.setCount(itemInBasket.getCount() + item.getCount());
                // Save the updated basket
                saveBasket(key, basket);
                // Return the updated basket
                return basket;
            }
        }

        // Add the item to the basket
        basket.getItems().add(item);
        // Save the updated basket
        saveBasket(key, basket);
        // Return updated basket
        return basket;
    }

    public Basket removeItem(Principal userPrincipal, String productId) {
        String key = getPrincipalBasketKey(userPrincipal);
        Basket basket = getBasket(userPrincipal);

        List<Item> items = basket.getItems();
        for (Item item : items) {
            if (productId.equals(item.getProductId())) {
                items.remove(item);
                break;
            }
        }

        saveBasket(key, basket);
        return basket;
    }


    public Basket changeCount(Principal userPrincipal, String productId, Item updatedItem) {
        String key = getPrincipalBasketKey(userPrincipal);
        Basket basket = getBasket(userPrincipal);

        List<Item> items = basket.getItems();
        for (Item item : items) {
            if (productId.equals(item.getProductId())) {
                item.setCount(updatedItem.getCount());
                break;
            }
        }

        saveBasket(key, basket);
        return basket;
    }

    private String getPrincipalBasketKey(Principal userPrincipal) {
        return String.format("basket:%s", userPrincipal.getName());
    }

    private Basket loadBasket(String key) {
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

    private void saveBasket(String key, Basket basket) {
        // Clear
        redisDS.key().del(key);

        // Serialize items to JSON
        List<String> jsonItems = new ArrayList<>();
        for (Item item : basket.getItems()) {
            String jsonItem = null;
            try {
                jsonItem = objectMapper.writeValueAsString(item);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            jsonItems.add(jsonItem);
        }

        // Push to the list
        for (String jsonItem : jsonItems) {
            stringListCommands.rpush(key, jsonItem);
        }

        redisDS.key().expire(key, 120);
    }
}
