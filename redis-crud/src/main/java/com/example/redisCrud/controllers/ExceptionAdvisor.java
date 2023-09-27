package com.example.redisCrud.controllers;

import com.example.common.exceptions.AccountNotFoundException;
import com.example.common.exceptions.FoodNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackageClasses = {AccountController.class, FoodController.class})
public class ExceptionAdvisor {

    @ExceptionHandler(FoodNotFoundException.class)
    public ResponseEntity<String> foodNotFoundException(FoodNotFoundException foodNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(foodNotFoundException.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> accountNotFoundException(AccountNotFoundException accountNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(accountNotFoundException.getMessage());
    }
}
