package com.example.redisStudy.controllers;

import com.example.redisStudy.model.Food;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/v1/food")
public class FoodController {

    @Autowired
    RedisTemplate<String, Food> redisFoodTemplate;

    @PostMapping
    public Food createFood(@RequestBody Food food) throws Exception {
        if(food == null) {
            throw new Exception("No food sent on request payload.");
        }

        if(food.getFoodId() == null) {
            food.setFoodId(UUID.randomUUID().toString());
        }

        redisFoodTemplate.opsForValue().set("food:" + food.getFoodId(), food, 1, TimeUnit.DAYS);

        log.info("[POST] creating food with id={}", food.getFoodId());
        return food;
    }

    @GetMapping("/{id}")
    public Food getFood(@PathVariable("id") String foodId) {
        log.info("[GET] retrieving food with id={}", foodId);
        return redisFoodTemplate.opsForValue().get("food:" + foodId);
    }

    @PutMapping("/{id}")
    public void updateFood(@PathVariable("id") String foodId, @RequestBody Food food) {
        log.info("[PUT] updating food with id={}", foodId);
        redisFoodTemplate.opsForValue().set("food:" + foodId, food, 1, TimeUnit.DAYS);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable(name = "id") String foodId) {
        log.info("[DELETE] removing food with id={}", foodId);
        redisFoodTemplate.delete("food:" + foodId);
    }

}
