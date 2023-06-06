package de.berlin.htw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.jupiter.api.Test;

import de.berlin.htw.entity.dao.UserRepository;
import de.berlin.htw.entity.dto.UserEntity;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class UserRepositoryTest {

    @PersistenceContext
    EntityManager entityManager;
    
    @Inject
    UserTransaction userTransaction;
    
    @Inject
    UserRepository repository;
    
    @Test
    void testAddUser() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        UserEntity user = new UserEntity();
        user.setName("TestUser");
        user.setBalance(10f);
        repository.persistUser(user);
        
        userTransaction.begin();
//        entityManager.joinTransaction();
        int deltedUser = entityManager
                .createQuery("DELETE FROM UserEntity u WHERE u.name = :userName")
                .setParameter("userName", user.getName())
                .executeUpdate();
        userTransaction.commit();
        assertEquals(1, deltedUser);
    }

}
