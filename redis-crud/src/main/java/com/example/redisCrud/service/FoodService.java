package com.example.redisCrud.service;

import com.example.common.exceptions.FoodNotFoundException;
import com.example.redisCrud.metrics.MetricsHelper;
import com.example.redisCrud.model.Food;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FoodService {

    RedisTemplate<String, Food> redisFoodTemplate;
    MetricsHelper metricsHelper;

    public FoodService(RedisTemplate<String, Food> redisFoodTemplate, MetricsHelper metricsHelper) {
        this.redisFoodTemplate = redisFoodTemplate;
        this.metricsHelper = metricsHelper;
    }

    public Food createFood(Food food) {

        if(food.getFoodId() == null) {
            food.setFoodId(UUID.randomUUID().toString());
        }

        long startTime = Instant.now().toEpochMilli();
        redisFoodTemplate.opsForValue().set("food:" + food.getFoodId(), food, 1, TimeUnit.DAYS);
        metricsHelper.timer("redis.storage.timer", "create_food", Instant.now().toEpochMilli() - startTime);

        log.info("[POST] saved food with id={}", food.getFoodId());
        return food;
    }

    public Food getFood(String foodId) throws FoodNotFoundException {
        log.info("[GET] retrieving food with id={}", foodId);
        long startTime = Instant.now().toEpochMilli();
        Food retrievedFood = Optional.ofNullable(
                        redisFoodTemplate.opsForValue().get("food:" + foodId))
                .filter(Objects::nonNull)
                .orElseThrow(() -> new FoodNotFoundException(String.format("Food with id=%s not found", foodId)));
        metricsHelper.timer("redis.storage.timer", "get_food", Instant.now().toEpochMilli() - startTime);
        return retrievedFood;
    }

    public boolean updateFood(String foodId, Food newFood) {
        log.info("[PUT] updating food with id={}", foodId);
        newFood.setFoodId(foodId);
        try {
            long startTime = Instant.now().toEpochMilli();
            redisFoodTemplate.opsForValue().set("food:" + foodId, newFood, 1, TimeUnit.DAYS);
            metricsHelper.timer("redis.storage.timer", "update_food", Instant.now().toEpochMilli() - startTime);
        } catch (Exception e) {
            log.error("[PUT] Failed to update foodId={}. ", foodId, e);
            return false;
        }
        log.info("[PUT] updated food with id={}", foodId);
        return true;
    }

    public boolean deleteFood(String foodId) {
        // Todo: improve method for cases where delete operation returns FALSE.
        //  Currently, the TRUE return value can be misleading
        try {
            long startTime = Instant.now().toEpochMilli();
            redisFoodTemplate.delete("food:" + foodId);
            metricsHelper.timer("redis.storage.timer", "delete_food", Instant.now().toEpochMilli() - startTime);
        } catch (Exception e) {
            log.error("[DELETE] Failed to delete foodId={}. ", foodId, e);
            return false;
        }
        log.info("[DELETE] removed food with id={}", foodId);
        return true;
    }
}
