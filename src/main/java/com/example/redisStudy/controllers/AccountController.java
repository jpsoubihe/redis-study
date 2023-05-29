package com.example.redisStudy.controllers;

import com.example.redisStudy.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/v1/account")
public class AccountController {

    @Autowired
    RedisTemplate<String, Account> redisAccountTemplate;

    @PostMapping
    public String createAccount(@RequestBody Account account) throws Exception {

        if(account == null) {
            throw new Exception("No account sent on request payload.");
        }

        if(account.getAccountId() == null) {
            account.setAccountId(UUID.randomUUID().toString());
        }

        redisAccountTemplate.opsForValue().set("accounts:" + account.getAccountId(), account, 1, TimeUnit.DAYS);

        log.info("[POST] creating account with id={}", account.getAccountId());
        return account.getAccountId();
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable(name = "id") String accountId) {
        log.info("[GET] retrieving account with id={}", accountId);
        return redisAccountTemplate.opsForValue().get("accounts:" + accountId);
    }

    @PutMapping("/{id}")
    public void updateAccount(@PathVariable(name = "id") String accountId, @RequestBody Account account) {
        log.info("[PUT] updating account with id={}", accountId);
        Account previousAccount = redisAccountTemplate.opsForValue().get("accounts:" + accountId);
        if(previousAccount != null) {
            account.setAccountId(accountId);
        }
        redisAccountTemplate.opsForValue().set("accounts:" + account.getAccountId(), account, 1, TimeUnit.DAYS);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable(name = "id") String accountId) {
        log.info("[DELETE] removing account with id={}", accountId);
        redisAccountTemplate.delete("accounts:" + accountId);
    }
}
