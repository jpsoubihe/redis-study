package com.example.redisCrud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Food {

    String foodId;

    String foodName;

    List<String> foodIngredients;

    FoodType foodType;
}
