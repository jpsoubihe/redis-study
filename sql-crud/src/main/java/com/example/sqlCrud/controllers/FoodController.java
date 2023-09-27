package com.example.sqlCrud.controllers;

import com.example.common.exceptions.FoodNotFoundException;
import com.example.sqlCrud.model.Food;
import com.example.sqlCrud.service.FoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @Autowired
    FoodService foodService;

    @PostMapping
    public ResponseEntity<Food> createFood(@RequestBody Food food) {
        log.info("[POST] processing request for food with id={}", food.getFoodId());
        return ResponseEntity.ok(foodService.createFood(food));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Food> getFood(@PathVariable("id") String foodId) throws FoodNotFoundException {
        log.info("[GET] processing request for food with id={}", foodId);
        return ResponseEntity.ok(foodService.getFood(foodId));
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
