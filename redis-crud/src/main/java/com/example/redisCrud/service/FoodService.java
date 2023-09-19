package com.example.redisCrud.service;

import com.example.common.exceptions.FoodNotFoundException;
import com.example.redisCrud.model.Food;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FoodService {

    @Autowired
    RedisTemplate<String, Food> redisFoodTemplate;

    public Food createFood(Food food) {

        if(food.getFoodId() == null) {
            food.setFoodId(UUID.randomUUID().toString());
        }

        redisFoodTemplate.opsForValue().set("food:" + food.getFoodId(), food, 1, TimeUnit.DAYS);

        log.info("[POST] saved food with id={}", food.getFoodId());
        return food;
    }

    public Food getFood(String foodId) throws FoodNotFoundException {
        log.info("[GET] retrieving food with id={}", foodId);
        return Optional.ofNullable(
                        redisFoodTemplate.opsForValue().get("food:" + foodId))
                .filter(Objects::nonNull)
                .orElseThrow(() -> new FoodNotFoundException(String.format("Food with id=%s not found", foodId)));
    }

    public boolean updateFood(String foodId, Food newFood) {
        log.info("[PUT] updating food with id={}", foodId);
        newFood.setFoodId(foodId);
        try {
            redisFoodTemplate.opsForValue().set("food:" + foodId, newFood, 1, TimeUnit.DAYS);
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
            redisFoodTemplate.delete("food:" + foodId);
        } catch (Exception e) {
            log.error("[DELETE] Failed to delete foodId={}. ", foodId, e);
            return false;
        }
        log.info("[DELETE] removed food with id={}", foodId);
        return true;
    }
}
