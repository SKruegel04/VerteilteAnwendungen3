package de.berlin.htw.control;

import de.berlin.htw.boundary.dto.Item;
import de.berlin.htw.boundary.dto.Order;
import de.berlin.htw.entity.dao.OrderRepository;
import de.berlin.htw.entity.dto.ItemEntity;
import de.berlin.htw.entity.dto.OrderEntity;
import de.berlin.htw.entity.dto.UserEntity;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import de.berlin.htw.boundary.dto.Orders;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@Dependent
public class OrderController {

    @Inject
    private OrderRepository orderRepository;

    @Transactional
    public Orders getCompletedOrders(Principal userPrincipal) {
        List<OrderEntity> orderEntities = orderRepository.findAll();
        List<Order> orders = orderEntities.stream()
            .map(this::orderFromEntity)
            .collect(Collectors.toList());
        Orders result = new Orders();
        result.setOrders(orders);
        result.setBalance(((UserEntity) userPrincipal).getBalance());
        return result;
    }

    public void create(Order order) {
        OrderEntity orderEntity = orderToEntity(order);
        orderRepository.persistOrder(orderEntity);
    }

    private ItemEntity itemToEntity(Item item) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setProductId(item.getProductId());
        itemEntity.setProductName(item.getProductName());
        itemEntity.setCount(item.getCount());
        itemEntity.setPrice(item.getPrice());
        return itemEntity;
    }

    private Item itemFromEntity(ItemEntity itemEntity) {
        Item item = new Item();
        item.setProductId(itemEntity.getProductId());
        item.setProductName(itemEntity.getProductName());
        item.setCount(itemEntity.getCount());
        item.setPrice(itemEntity.getPrice());
        return item;
    }

    private OrderEntity orderToEntity(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setItems(
            order.getItems().stream()
                .map(this::itemToEntity)
                .collect(Collectors.toList())
        );
        orderEntity.setTotal(order.getTotal());
        return orderEntity;
    }

    private Order orderFromEntity(OrderEntity orderEntity) {
        Order order = new Order();
        order.setItems(
            orderEntity.getItems().stream()
                .map(this::itemFromEntity)
                .collect(Collectors.toList())
        );
        order.setTotal(orderEntity.getTotal());
        return order;
    }
}
