package com.example.redisStudy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FoodNotFoundException extends Exception {
    public FoodNotFoundException(String message) {
        super(message);
    }
}
