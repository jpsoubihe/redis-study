package com.example.sqlCrud.unit;

import com.example.common.exceptions.FoodNotFoundException;
import com.example.sqlCrud.metrics.MetricsHelper;
import com.example.sqlCrud.model.Food;
import com.example.sqlCrud.model.FoodType;
import com.example.sqlCrud.repositories.FoodRepository;
import com.example.sqlCrud.service.FoodService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class FoodServiceTest {

    @InjectMocks
    FoodService foodService;

    @Mock
    FoodRepository foodRepository;

    @Mock
    MetricsHelper metricsHelper;

    protected ArgumentCaptor<Food> argumentCaptor = ArgumentCaptor.forClass(Food.class);

    private String foodId;

    @BeforeEach
    public void setUp() {
        foodId = UUID.randomUUID().toString();
    }

    @Test
    void shouldSuccessfullyCreateFood() {
        String foodName = "rice";
        List<String> foodIngredients = List.of("wheat");
        Food toCreate = Food.builder()
                .foodId(foodId)
                .foodName(foodName)
                .foodType(FoodType.SALTY)
                .foodIngredients(foodIngredients)
                .build();
        Mockito.when(foodRepository.save(toCreate))
                .thenReturn(toCreate);

        Food created = foodService.createFood(toCreate);
        Assertions.assertNotNull(created);
        Assertions.assertEquals(foodId, created.getFoodId());
        Assertions.assertEquals(foodName, created.getFoodName());
        Assertions.assertEquals(FoodType.SALTY, created.getFoodType());
        Assertions.assertEquals(foodIngredients, created.getFoodIngredients());
    }

    @Test
    void shouldSuccessfullyCreateFoodWithNoIndicatedId() {
        String foodName = "rice";
        List<String> foodIngredients = List.of("wheat");
        Food toCreate = Food.builder()
                .foodName(foodName)
                .foodType(FoodType.SALTY)
                .foodIngredients(foodIngredients)
                .build();

        Mockito.when(foodRepository.save(argumentCaptor.capture())).thenReturn(toCreate);
        Food retrievedFood = foodService.createFood(toCreate);
        Assertions.assertNotNull(argumentCaptor.getValue().getFoodId());
        Assertions.assertEquals(foodName, retrievedFood.getFoodName());
        Assertions.assertEquals(FoodType.SALTY, retrievedFood.getFoodType());
        Assertions.assertEquals(foodIngredients, retrievedFood.getFoodIngredients());
    }

    @Test
    void shouldSuccessfullyGetFood() throws FoodNotFoundException {
        String foodName = "rice";
        List<String> foodIngredients = List.of("wheat");
        Food food = Food.builder()
                .foodName(foodName)
                .foodType(FoodType.SALTY)
                .foodIngredients(foodIngredients)
                .build();
        Mockito.when(foodRepository.findFoodByFoodId(foodId))
                .thenReturn(Optional.of(food));
        foodService.getFood(foodId);
    }

    @Test
    void shouldThrowFoodNotFoundExceptionIfNoFoodIsFound() {
        Mockito.when(foodRepository.findFoodByFoodId(foodId))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(FoodNotFoundException.class, () -> foodService.getFood(foodId));
    }

    @Test
    void shouldSuccessfullyUpdateFood() {
        String foodName = "rice";
        List<String> foodIngredients = new ArrayList<>();
        foodIngredients.add("olive oil");
        Food food = Food.builder()
                .foodId(foodId)
                .foodName(foodName)
                .foodType(FoodType.SALTY)
                .foodIngredients(foodIngredients)
                .build();
        foodIngredients.add(0, "wheat");
        Food newFood = Food.builder()
                .foodName(foodName)
                .foodType(FoodType.SALTY)
                .foodIngredients(foodIngredients)
                .build();

        Mockito.when(foodRepository.findFoodByFoodId(foodId))
                .thenReturn(Optional.of(food));
        Assertions.assertTrue(foodService.updateFood(foodId, newFood));
        Mockito.verify(foodRepository).findFoodByFoodId(foodId);
        Mockito.verify(foodRepository).save(argumentCaptor.capture());
        Food capturedFood = argumentCaptor.getValue();
        Assertions.assertNotNull(capturedFood);
        Assertions.assertEquals(foodId, capturedFood.getFoodId());
    }

    @Test
    void shouldSuccessfullyCreateFoodWhenUpdateOnNotFoundFoodId() {
        String foodName = "rice";
        List<String> foodIngredients = List.of("wheat");
        Food newFood = Food.builder()
                .foodName(foodName)
                .foodType(FoodType.SALTY)
                .foodIngredients(foodIngredients)
                .build();

        Mockito.when(foodRepository.findFoodByFoodId(foodId))
                .thenReturn(Optional.empty());
        Mockito.when(foodRepository.save(argumentCaptor.capture())).thenReturn(newFood);
        Assertions.assertTrue(foodService.updateFood(foodId, newFood));
        Mockito.verify(foodRepository).findFoodByFoodId(foodId);
        Food capturedFood = argumentCaptor.getValue();
        Mockito.verify(foodRepository).save(capturedFood);
        Assertions.assertNotNull(capturedFood);
        Assertions.assertEquals(foodId, capturedFood.getFoodId());
        Assertions.assertEquals(FoodType.SALTY, capturedFood.getFoodType());
        Assertions.assertEquals(foodIngredients, capturedFood.getFoodIngredients());
    }

    @Test
    void shouldSuccessfullyDeleteFood() {
        Assertions.assertTrue(foodService.deleteFood(foodId));
    }

    @Test
    void shouldReturnFalseWhenDeletionFails() {
        Mockito.doThrow(RuntimeException.class).when(foodRepository).deleteById(foodId);
        Assertions.assertFalse(foodService.deleteFood(foodId));
    }


}
