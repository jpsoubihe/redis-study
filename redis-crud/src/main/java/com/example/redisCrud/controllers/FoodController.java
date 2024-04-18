package com.example.redisCrud.controllers;

import com.example.common.exceptions.FoodNotFoundException;
import com.example.redisCrud.metrics.MetricsHelper;
import com.example.redisCrud.model.Food;
import com.example.redisCrud.service.FoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/v1/food")
public class FoodController {

    FoodService foodService;

    MetricsHelper metricsHelper;

    public FoodController(FoodService foodService, MetricsHelper metricsHelper) {
        this.foodService = foodService;
        this.metricsHelper = metricsHelper;
    }

    @PostMapping
    public ResponseEntity<Food> createFood(@RequestBody Food food) throws Exception {
        long startTime = Instant.now().toEpochMilli();
        metricsHelper.counter("redis.request.counter", "create_food");
        log.info("[POST] processing request for food with id={}", food.getFoodId());
        Food createdFood = foodService.createFood(food);
        metricsHelper.timer("redis.request.timer", "create_food", Instant.now().toEpochMilli() - startTime);
        return ResponseEntity.ok(createdFood);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Food> getFood(@PathVariable("id") String foodId) throws FoodNotFoundException {
        long startTime = Instant.now().toEpochMilli();
        metricsHelper.counter("redis.request.counter", "get_food");
        log.info("[GET] processing request for food with id={}", foodId);
        Food food = foodService.getFood(foodId);
        metricsHelper.timer("redis.request.timer", "get_food", Instant.now().toEpochMilli() - startTime);
        return ResponseEntity.ok(food);
    }

    @PutMapping("/{id}")
    public void updateFood(@PathVariable("id") String foodId, @RequestBody Food food) {
        long startTime = Instant.now().toEpochMilli();
        metricsHelper.counter("redis.request.counter", "update_food");
        log.info("[PUT] processing request for food with id={}", foodId);
        foodService.updateFood(foodId, food);
        metricsHelper.timer("redis.request.timer", "update_food", Instant.now().toEpochMilli() - startTime);
    }

    @DeleteMapping("/{id}")
    public void deleteFood(@PathVariable(name = "id") String foodId) {
        long startTime = Instant.now().toEpochMilli();
        metricsHelper.counter("redis.request.counter", "delete_food");
        log.info("[DELETE] processing request for food with id={}", foodId);
        foodService.deleteFood(foodId);
        metricsHelper.timer("redis.request.timer", "delete_food", Instant.now().toEpochMilli() - startTime);
    }

}
