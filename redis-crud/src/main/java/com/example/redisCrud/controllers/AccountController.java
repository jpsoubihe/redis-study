package com.example.redisCrud.controllers;

import com.example.common.exceptions.AccountNotFoundException;
import com.example.redisCrud.metrics.MetricsHelper;
import com.example.redisCrud.model.Account;
import com.example.redisCrud.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/v1/account")
public class AccountController {

    AccountService accountService;

    MetricsHelper metricsHelper;

    public AccountController(AccountService accountService, MetricsHelper metricsHelper) {
        this.accountService = accountService;
        this.metricsHelper = metricsHelper;
    }

    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody Account account) {
        long startTime = Instant.now().toEpochMilli();
        metricsHelper.counter("redis.request.counter", "create_account");
        log.info("[POST] processing request for account with id={}", account.getAccountId());
        String accountId = accountService.createAccount(account);
        metricsHelper.timer("redis.request.timer", "create_account", Instant.now().toEpochMilli() - startTime);
        return ResponseEntity.ok(accountId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable(name = "id") String accountId) throws AccountNotFoundException {
        long startTime = Instant.now().toEpochMilli();
        metricsHelper.counter("redis.request.counter", "get_account");
        log.info("[GET] processing request for account with id={}", accountId);
        Account account = accountService.getAccount(accountId);
        metricsHelper.timer("redis.request.timer", "get_account", Instant.now().toEpochMilli() - startTime);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}")
    public void updateAccount(@PathVariable(name = "id") String accountId, @RequestBody Account account) {
        long startTime = Instant.now().toEpochMilli();
        metricsHelper.counter("redis.request.counter", "update_account");
        log.info("[PUT] processing request account with id={}", accountId);
        accountService.updateAccount(accountId, account);
        metricsHelper.timer("redis.request.timer", "update_account", Instant.now().toEpochMilli() - startTime);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable(name = "id") String accountId) {
        long startTime = Instant.now().toEpochMilli();
        metricsHelper.counter("redis.request.counter", "delete_account");
        log.info("[DELETE] processing request for account with id={}", accountId);
        accountService.deleteAccount(accountId);
        metricsHelper.timer("redis.request.timer", "delete_account", Instant.now().toEpochMilli() - startTime);
    }
}
