package de.berlin.htw.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.berlin.htw.boundary.dto.Basket;
import de.berlin.htw.boundary.dto.Item;
import de.berlin.htw.boundary.dto.Order;
import de.berlin.htw.entity.dao.UserRepository;
import de.berlin.htw.entity.dto.UserEntity;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.list.ListCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@Dependent
public class UserController {
    @Inject
    UserRepository userRepository;

    public boolean hasBalance(Principal userPrincipal, Float price) {
        UserEntity userEntity = (UserEntity) userPrincipal;
        return userEntity.getBalance() >= price;
    }

    @Transactional
    public void updateBalance(Principal userPrincipal, Float balance) {
        UserEntity userEntity = (UserEntity) userPrincipal;
        userEntity.setBalance(balance);
        userRepository.updateUser(userEntity);
    }
}
