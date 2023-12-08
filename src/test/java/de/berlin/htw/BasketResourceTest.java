package de.berlin.htw;

import de.berlin.htw.boundary.dto.Item;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

@QuarkusTest
class BasketResourceTest {

    @Inject
    protected RedisDataSource redisDS;
    
    @Test
    void testGetBasket() {
        ValueCommands<String, Integer> countCommands = redisDS.value(Integer.class);
        
        given()
            .log().all()
            .when().header("X-User-Id", "2")
            .get("/basket")
            .then()
            .log().all()
            .statusCode(415);
        
        assertEquals(88, countCommands.get("TODO"));
    }

    @Test
    void testAddItem() {
        final Item item = new Item();
        item.setProductName("Test Name");
        item.setProductId("1-2-3-4-5-6");
        item.setPrice(14.0f);

        given()
                .log().all()
                .when().header("X-User-Id", "3")
                .contentType(ContentType.JSON)
                .body(item)
                .post("/basket/anyID")
                .then()
                .log().all()
                .statusCode(201);
    }

    @Test
    void testCheckout() {
        given()
            .log().all()
            .when().header("X-User-Id", "4")
            .post("/basket")
            .then()
            .log().all()
            .statusCode(201)
            .header("Location", "http://localhost:8081/hierFehltNoEtwas");
    }

    @Test
    void testProductNameMaxLengthValidation() {
        final Item item = new Item();
        item.setProductName("A".repeat(256));
        item.setProductId("1-2-3-4-5-6");
        item.setPrice(14.0f);

        given()
            .log().all()
            .when().header("X-User-Id", "3")
            .contentType(ContentType.JSON)
            .body(item)
            .post("/basket/anyID")
            .then()
            .log().all()
            .statusCode(400);
    }

    @Test
    void testProductIdValidationA() {
        final Item item = new Item();
        item.setProductName("Test");
        item.setProductId("1-2-3-4-5");
        item.setPrice(14.0f);

        given()
            .log().all()
            .when().header("X-User-Id", "3")
            .contentType(ContentType.JSON)
            .body(item)
            .post("/basket/anyID")
            .then()
            .log().all()
            .statusCode(400);
    }

    @Test
    void testProductIdValidationB() {
        final Item item = new Item();
        item.setProductName("Test");
        item.setProductId("133322");
        item.setPrice(14.0f);

        given()
            .log().all()
            .when().header("X-User-Id", "3")
            .contentType(ContentType.JSON)
            .body(item)
            .post("/basket/anyID")
            .then()
            .log().all()
            .statusCode(400);
    }

    @Test
    void testPriceMinValidation() {
        final Item item = new Item();
        item.setProductName("Test");
        item.setProductId("1-2-3-4-5-6");
        item.setPrice(8.0f);

        given()
            .log().all()
            .when().header("X-User-Id", "3")
            .contentType(ContentType.JSON)
            .body(item)
            .post("/basket/anyID")
            .then()
            .log().all()
            .statusCode(400);
    }
    @Test
    void testPriceMaxValidation() {
        final Item item = new Item();
        item.setProductName("Test");
        item.setProductId("1-2-3-4-5-6");
        item.setPrice(120f);

        given()
            .log().all()
            .when().header("X-User-Id", "3")
            .contentType(ContentType.JSON)
            .body(item)
            .post("/basket/anyID")
            .then()
            .log().all()
            .statusCode(400);
    }

}