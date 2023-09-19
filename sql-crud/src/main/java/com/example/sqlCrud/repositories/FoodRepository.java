package com.example.sqlCrud.repositories;

import com.example.sqlCrud.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, String> {
    Optional<Food> findFoodByFoodId(String foodId);
}
