package de.berlin.htw.entity.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import de.berlin.htw.entity.dto.UserEntity;

/**
 * @author Alexander Stanik [stanik@htw-berlin.de]
 */
@ApplicationScoped
public class UserRepository {

    @PersistenceContext
    EntityManager entityManager;
    
    public UserEntity findUserById(final Integer id) {
        return entityManager.find(UserEntity.class, id);
    }
    
    @Transactional
    public void persistUser(final UserEntity user) {
        entityManager.persist(user);
    }
    
}