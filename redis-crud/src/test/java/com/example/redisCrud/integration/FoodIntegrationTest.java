package com.example.redisCrud.integration;

import com.example.redisCrud.controllers.FoodController;
import com.example.redisCrud.model.Food;
import com.example.redisCrud.model.FoodType;
import com.example.redisCrud.service.FoodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static com.example.redisCrud.integration.TestConstants.BASE_FOOD_URL_SUFFIX;
import static com.example.redisCrud.integration.TestConstants.BASE_URL;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FoodIntegrationTest {

    @Autowired
    RedisTemplate<String, Food> foodRedisTemplate;

    @Autowired
    FoodService foodService;

    @Autowired
    FoodController foodController;

    @Test
    public void saveRetrieveAndDeleteFoodSuccessfully() {

        String foodId = UUID.randomUUID().toString();
        Food testFood = Food.builder()
                .foodId(foodId)
                .foodType(FoodType.SALTY)
                .foodName("Coxinha")
                .build();

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .post()
                .uri("/v1/food")
                .bodyValue(testFood)
                .exchange()
                .expectBody(Food.class)
                .isEqualTo(testFood);

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .get()
                .uri(String.join("/", BASE_FOOD_URL_SUFFIX, foodId))
                .exchange()
                .expectBody(Food.class)
                .isEqualTo(testFood);

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .delete()
                .uri(String.join("/", BASE_FOOD_URL_SUFFIX, foodId))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    public void deleteNonExistentFoodShouldReturn2xx() {
        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .delete()
                .uri(String.join("/", BASE_FOOD_URL_SUFFIX, UUID.randomUUID().toString()))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    public void getNonExistentFoodShouldReturn404NotFoundStatus() {
        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .get()
                .uri("/v1/food/" + UUID.randomUUID())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void shouldUpdateExistentFoodSuccessfully() {
        String foodId = UUID.randomUUID().toString();

        Food testFood = Food.builder()
                .foodId(foodId)
                .foodType(FoodType.SALTY)
                .foodName("Coxinha")
                .build();

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .post()
                .uri(BASE_FOOD_URL_SUFFIX)
                .bodyValue(testFood)
                .exchange()
                .expectBody(Food.class)
                .isEqualTo(testFood);

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .get()
                .uri(String.join("/", BASE_FOOD_URL_SUFFIX, foodId))
                .exchange()
                .expectBody(Food.class)
                .isEqualTo(testFood);

        testFood.setFoodType(FoodType.FESTIVE);

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .put()
                .uri(String.join("/", BASE_FOOD_URL_SUFFIX, foodId))
                .bodyValue(testFood)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .get()
                .uri(String.join("/", BASE_FOOD_URL_SUFFIX, foodId))
                .exchange()
                .expectBody(Food.class)
                .isEqualTo(testFood);
    }

    @Test
    public void shouldCreateNewFoodWhenUpdateNonExistentFood() {
        String foodId = UUID.randomUUID().toString();

        Food toUpdateFood = Food.builder()
                .foodId(foodId)
                .foodType(FoodType.SALTY)
                .foodName("Coxinha")
                .build();

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .put()
                .uri(String.join("/", BASE_FOOD_URL_SUFFIX, foodId))
                .bodyValue(toUpdateFood)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .get()
                .uri(String.join("/", BASE_FOOD_URL_SUFFIX, foodId))
                .exchange()
                .expectBody(Food.class)
                .isEqualTo(toUpdateFood);
    }
}
