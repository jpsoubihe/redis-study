package com.example.redisStudy.controllers;

import com.example.redisStudy.model.Food;
import com.example.redisStudy.service.FoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.AttributeNotFoundException;

@Slf4j
@RestController
@RequestMapping("/v1/food")
public class FoodController {

    @Autowired
    FoodService foodService;

    @PostMapping
    public Food createFood(@RequestBody Food food) throws Exception {
        log.info("[POST] processing request for food with id={}", food.getFoodId());
        return foodService.createFood(food);
    }

    @GetMapping("/{id}")
    public Food getFood(@PathVariable("id") String foodId) throws AttributeNotFoundException {
        log.info("[GET] processing request for food with id={}", foodId);
        return foodService.getFood(foodId);
    }

    @PutMapping("/{id}")
    public void updateFood(@PathVariable("id") String foodId, @RequestBody Food food) {
        log.info("[PUT] processing request for food with id={}", foodId);
        foodService.updateFood(foodId, food);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable(name = "id") String foodId) {
        log.info("[DELETE] processing request for food with id={}", foodId);
        foodService.deleteFood(foodId);
    }

}
