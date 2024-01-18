package de.berlin.htw;

import de.berlin.htw.boundary.dto.Basket;
import de.berlin.htw.boundary.dto.Item;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import jakarta.enterprise.inject.build.compatible.spi.SkipIfPortableExtensionPresent;
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
        given()
            .log().all()
            .when().header("X-User-Id", "2")
            .get("/basket")
            .then()
            .log().all()
            .statusCode(200);
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

    //repeatedly add same item to see if count increase

    /*@Test
    void testAddItemTwice() {
        final Item item = new Item();
        item.setProductName("Test Name");
        item.setProductId("1-2-3-4-5-6");
        item.setPrice(14.0f);
        item.setCount(1);

        given()
                .log().all()
                .when().header("X-User-Id", "3")
                .contentType(ContentType.JSON)
                .body(item)
                .post("/basket/anyID")
                .then()
                .log().all()
                .statusCode(201);

        given()
                .log().all()
                .when().header("X-User-Id", "3")
                .contentType(ContentType.JSON)
                .body(item)
                .post("/basket/anyID")
                .then()
                .log().all()
                .statusCode(201);

        // Get the basket and check if count is 2
        Basket basket = given()
                .log().all()
                .when().header("X-User-Id", "3")
                .get("/basket/anyID")
                .then()
                .log().all()
                .statusCode(200)
                .extract().as(Basket.class);

        int count = basket.getItems().stream()
                .filter(basketItem -> basketItem.getProductId().equals(item.getProductId()))
                .mapToInt(Item::getCount)
                .sum();

        assertEquals(2, count);
    }
    */



    @Test
    void testCheckout() {
        given()
            .log().all()
            .when().header("X-User-Id", "4")
            .post("/basket")
            .then()
            .log().all()
            .statusCode(201)
            .header("Location", "http://localhost:8081/orders");
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