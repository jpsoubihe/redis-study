package com.example.redisStudy.controllers;

import com.example.redisStudy.model.Food;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/food")
public class FoodController {

    @PostMapping
    public Food createFood(@RequestBody Food food) {
        log.info("[POST] creating food with id={}", food.getFoodId());
        return food;
    }

    @GetMapping("/{id}")
    public Food getFood(@PathVariable("id") String foodId) {
        log.info("[GET] retrieving food with id={}", foodId);
        return Food.builder().build();
    }

    @PutMapping("/{id}")
    public void updateFood(@PathVariable("id") String foodId) {
        log.info("[PUT] updating account with id={}", foodId);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable(name = "id") String foodId) {
        log.info("[DELETE] removing food with id={}", foodId);
    }

}
