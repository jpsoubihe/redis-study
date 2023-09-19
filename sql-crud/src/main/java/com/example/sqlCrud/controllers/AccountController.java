package com.example.sqlCrud.controllers;

import com.example.common.exceptions.AccountNotFoundException;
import com.example.sqlCrud.model.Account;
import com.example.sqlCrud.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody Account account) {
        log.info("[POST] processing request for account with id={}", account.getAccountId());
        return ResponseEntity.ok(accountService.createAccount(account));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable(name = "id") String accountId) throws AccountNotFoundException {
        log.info("[GET] processing request for account with id={}", accountId);
        return ResponseEntity.ok(accountService.getAccount(accountId));
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
