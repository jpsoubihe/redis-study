package com.example.redisStudy.controllers;

import com.example.redisStudy.model.Account;
import com.example.redisStudy.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.AttributeNotFoundException;

@Slf4j
@RestController
@RequestMapping("/v1/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @PostMapping
    public String createAccount(@RequestBody Account account) {
        log.info("[POST] processing request for account with id={}", account.getAccountId());
        return accountService.createAccount(account);
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable(name = "id") String accountId) throws AttributeNotFoundException {
        log.info("[GET] processing request for account with id={}", accountId);
        return accountService.getAccount(accountId);
    }

    @PutMapping("/{id}")
    public void updateAccount(@PathVariable(name = "id") String accountId, @RequestBody Account account) {
        log.info("[PUT] processing request account with id={}", accountId);
        accountService.updateAccount(accountId, account);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable(name = "id") String accountId) {
        log.info("[DELETE] processing request for account with id={}", accountId);
        accountService.deleteAccount(accountId);
    }
}
