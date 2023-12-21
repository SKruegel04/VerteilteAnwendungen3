package de.berlin.htw.entity.dao;

import de.berlin.htw.entity.dto.OrderEntity;
import de.berlin.htw.entity.dto.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@ApplicationScoped
public class OrderRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<OrderEntity> findAll() {
        return entityManager.createQuery("SELECT o FROM OrderEntity o", OrderEntity.class)
            .getResultList();
    }

    @Transactional
    public void persistOrder(final OrderEntity order) {
        entityManager.persist(order);
    }

    @Transactional
    public void updateOrder(final OrderEntity order) {
        entityManager.merge(order);
    }
}