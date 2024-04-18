package com.example.sqlCrud.service;

import com.example.common.exceptions.FoodNotFoundException;
import com.example.sqlCrud.metrics.MetricsHelper;
import com.example.sqlCrud.model.Food;
import com.example.sqlCrud.repositories.FoodRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class FoodService {

    private FoodRepository foodRepository;

    private MetricsHelper metricsHelper;

    public FoodService(FoodRepository foodRepository, MetricsHelper metricsHelper) {
        this.foodRepository = foodRepository;
        this.metricsHelper = metricsHelper;
    }

    public Food createFood(Food food) {
        log.info("[POST] Starting to save food with id={}", food.getFoodId());
        if(food.getFoodId() == null) {
            food.setFoodId(UUID.randomUUID().toString());
        }
        long startTime = Instant.now().toEpochMilli();
        Food createdFood = foodRepository.save(food);
        metricsHelper.timer("sql.storage.timer", "create_food", Instant.now().minus(startTime, ChronoUnit.MILLIS).toEpochMilli());
        log.info("[POST] Saved food with id={}", food.getFoodId());
        return createdFood;
    }

    public Food getFood(String foodId) throws FoodNotFoundException {
        log.info("[GET] retrieving food with id={}", foodId);
        long startTime = Instant.now().toEpochMilli();
        Food food = foodRepository.findFoodByFoodId(foodId)
                .orElseThrow(() -> new FoodNotFoundException(String.format("Food with id=%s not found", foodId)));
        metricsHelper.timer("sql.storage.timer", "get_food", Instant.now().minus(startTime, ChronoUnit.MILLIS).toEpochMilli());
        return food;
    }

    public boolean updateFood(String foodId, Food newFood) {
        log.info("[PUT] updating food with id={}", foodId);
        long startTime = Instant.now().toEpochMilli();
        foodRepository.findFoodByFoodId(foodId)
                .ifPresentOrElse(
                        food -> foodRepository.save(updateFoodInformation(food, newFood)),
                        () -> {
                            newFood.setFoodId(foodId);
                            createFood(newFood);
                        });
        metricsHelper.timer("sql.storage.timer", "update_food", Instant.now().minus(startTime, ChronoUnit.MILLIS).toEpochMilli());
        return true;
    }

    public boolean deleteFood(String foodId) {
        log.info("[DELETE] Starting deletion of food with id={}", foodId);
        try {
            long startTime = Instant.now().toEpochMilli();
            foodRepository.deleteById(foodId);
            metricsHelper.timer("sql.storage.timer", "delete_food", Instant.now().minus(startTime, ChronoUnit.MILLIS).toEpochMilli());
            log.info("[DELETE] Successfully deleted food with id={}", foodId);
        } catch (Exception ex) {
            log.error("[DELETE] Error deleting food with id={}. Cause: {}", foodId, ex);
            return false;
        }
        return true;
    }

    private Food updateFoodInformation(Food foodRecord, Food newFood) {
        log.info("Complementing info for food record of id={}", foodRecord.getFoodId());
        Optional.ofNullable(newFood.getFoodName()).ifPresent(foodRecord::setFoodName);
        Optional.ofNullable(newFood.getFoodType()).ifPresent(foodRecord::setFoodType);
        // ToDo: this could be a complementary update, merging previous and new ingredients
        Optional.ofNullable(newFood.getFoodIngredients()).ifPresent(foodRecord::setFoodIngredients);
        return foodRecord;
    }
}
