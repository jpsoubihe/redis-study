package com.example.redisCrud.unit.service;

import com.example.common.exceptions.FoodNotFoundException;
import com.example.redisCrud.metrics.MetricsHelper;
import com.example.redisCrud.model.Food;
import com.example.redisCrud.model.FoodType;
import com.example.redisCrud.service.FoodService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    private static final String FOOD_REDIS_KEY_PREFIX = "food:";

    @InjectMocks
    FoodService foodService;

    @Mock
    RedisTemplate<String, Food> foodRedisTemplate;

    @Mock
    private ValueOperations<String, Food> valueOperations;

    @Mock
    MetricsHelper metricsHelper;

    private String foodId;

    @BeforeEach
    public void setup() {
        foodId = UUID.randomUUID().toString();
    }

    @Test
    public void shouldSuccessfullyCreateFood() {
        when(foodRedisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), any(), anyLong(), any());

        Food food = createStandardTestFood(foodId);

        Food createdFood = foodService.createFood(food);
        Assertions.assertEquals(food, createdFood);
    }

    @Test
    public void shouldSuccessfullyCreateFoodWithNewId() {
        when(foodRedisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), any(), anyLong(), any());

        Food food = createStandardTestFood(null);

        Food createdFood = foodService.createFood(food);

        Assertions.assertEquals(food.getFoodName(), createdFood.getFoodName());
        Assertions.assertEquals(food.getFoodType(), createdFood.getFoodType());
        Assertions.assertEquals(food.getFoodIngredients(), createdFood.getFoodIngredients());
    }

    @Test
    public void shouldSuccessfullyGetFood() throws FoodNotFoundException {
        when(foodRedisTemplate.opsForValue()).thenReturn(valueOperations);

        Food food = createStandardTestFood(foodId);
        when(valueOperations.get(FOOD_REDIS_KEY_PREFIX + foodId))
                .thenReturn(food);

        Food retrievedFood = foodService.getFood(foodId);
        Assertions.assertEquals(food, retrievedFood);
    }

    @Test
    public void shouldThrowExceptionIfElementNotFoundWhenGetFood() {
        when(foodRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(FOOD_REDIS_KEY_PREFIX + foodId)).thenReturn(null);

        Assertions.assertThrows(FoodNotFoundException.class, () -> foodService.getFood(foodId));
    }

    @Test
    public void shouldReturnTrueIfAccountSuccessfullyUpdated() {
        when(foodRedisTemplate.opsForValue()).thenReturn(valueOperations);
        Food food = createStandardTestFood(foodId);

        doNothing().when(valueOperations).set(FOOD_REDIS_KEY_PREFIX + foodId, food, 1L, TimeUnit.DAYS);

        Assertions.assertTrue(foodService.updateFood(foodId, food));
    }

    @Test
    public void shouldReturnFalseIfFoodUpdateFailed() {
        when(foodRedisTemplate.opsForValue()).thenReturn(valueOperations);
        Food food = createStandardTestFood(foodId);
        doThrow(RuntimeException.class).when(valueOperations)
                .set(anyString(), any(Food.class), anyLong(), any());

        Assertions.assertFalse(foodService.updateFood(foodId, food));
    }

    @Test
    public void shouldReturnTrueIfSuccessfullyDeleteFood() {
        when(foodRedisTemplate.delete(FOOD_REDIS_KEY_PREFIX + foodId)).thenReturn(true);
        Assertions.assertTrue(foodService.deleteFood(foodId));
    }

    @Test
    public void shouldReturnFalseIfDeleteFoodFailed() {
        when(foodRedisTemplate.delete(FOOD_REDIS_KEY_PREFIX + foodId)).thenThrow(RuntimeException.class);
        Assertions.assertFalse(foodService.deleteFood(foodId));
    }

    private Food createStandardTestFood(String createdFoodId) {
        return Food.builder()
                .foodId(createdFoodId)
                .foodName("Test food")
                .foodType(FoodType.SALTY)
                .foodIngredients(List.of("Test Ingredient"))
                .build();
    }
}