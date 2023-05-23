package com.example.redisStudy.controllers;

import com.example.redisStudy.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/account")
public class AccountController {

    @PostMapping
    public String createAccount() {
        String accountId = UUID.randomUUID().toString();
        log.info("[POST] creating account with id={}", accountId);
        return accountId;
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable(name = "id") String accountId) {
        log.info("[GET] retrieving account with id={}", accountId);
        return Account.builder()
                .accountId(accountId)
                .accountName("Test Account")
                .build();
    }

    @PutMapping("/{id}")
    public void updateAccount(@PathVariable(name = "id") String accountId) {
        log.info("[PUT] updating account with id={}", accountId);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable(name = "id") String accountId) {
        log.info("[DELETE] removing account with id={}", accountId);
    }
}
