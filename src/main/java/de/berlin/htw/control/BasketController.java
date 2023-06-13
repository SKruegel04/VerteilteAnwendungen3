package de.berlin.htw.control;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.NotSupportedException;

import de.berlin.htw.boundary.dto.Basket;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.quarkus.redis.datasource.list.ListCommands;

/**
 * @author Alexander Stanik [stanik@htw-berlin.de]
 */
@Dependent
public class BasketController {

    @Inject
    protected RedisDataSource redisDS;
    
    protected ValueCommands<String, Integer> countCommands;
    protected ListCommands<String, String> stringListCommands;
    
    @PostConstruct
    protected void init() {
        countCommands = redisDS.value(Integer.class);
        stringListCommands = redisDS.list(String.class);
    }
    
    public Basket todo() {
        countCommands.set("TODO", 88);
    	throw new NotSupportedException("TODO");
    }
}
