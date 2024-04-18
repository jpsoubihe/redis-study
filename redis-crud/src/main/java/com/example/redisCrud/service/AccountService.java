package com.example.redisCrud.service;

import com.example.common.exceptions.AccountNotFoundException;
import com.example.redisCrud.metrics.MetricsHelper;
import com.example.redisCrud.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AccountService {

    @Autowired
    RedisTemplate<String, Account> redisAccountTemplate;

    MetricsHelper metricsHelper;

    public AccountService(RedisTemplate<String, Account> redisAccountTemplate, MetricsHelper metricsHelper) {
        this.redisAccountTemplate = redisAccountTemplate;
        this.metricsHelper = metricsHelper;
    }

    public String createAccount(Account account) {
        if(account.getAccountId() == null) {
            account.setAccountId(UUID.randomUUID().toString());
        }

        long startTime = Instant.now().toEpochMilli();
        redisAccountTemplate.opsForValue().set("accounts:" + account.getAccountId(), account, 1, TimeUnit.DAYS);
        metricsHelper.timer("redis.storage.timer", "create_account", Instant.now().minus(startTime, ChronoUnit.MILLIS).toEpochMilli());

        log.info("[POST] saved account with id={}", account.getAccountId());

        return account.getAccountId();
    }

    public Account getAccount(String accountId) throws AccountNotFoundException {
        log.info("[GET] retrieving account with id={}", accountId);
        long startTime = Instant.now().toEpochMilli();
        Account retrievedAccount = Optional.ofNullable(
                redisAccountTemplate.opsForValue().get("accounts:" + accountId))
                .filter(Objects::nonNull)
                .orElseThrow(() -> new AccountNotFoundException(String.format("Account with id=%s not found.", accountId)));
        metricsHelper.timer("redis.storage.timer", "get_account", Instant.now().minus(startTime, ChronoUnit.MILLIS).toEpochMilli());
        return retrievedAccount;
    }

    public boolean updateAccount(String accountId, Account newAccount) {
        log.info("[PUT] updating account with id={}", accountId);

        newAccount.setAccountId(accountId);
        long startTime = Instant.now().toEpochMilli();
        try {
            redisAccountTemplate.opsForValue().set("accounts:" + newAccount.getAccountId(), newAccount, 1, TimeUnit.DAYS);
            metricsHelper.timer("redis.storage.timer", "update_account", Instant.now().minus(startTime, ChronoUnit.MILLIS).toEpochMilli());
        } catch (Exception e) {
            log.error("[PUT] Failed to update accountId={}. ", accountId, e);
            return false;

        }
        return true;
    }

    public boolean deleteAccount(String accountId) {
        try {
            long startTime = Instant.now().toEpochMilli();
            redisAccountTemplate.delete("accounts:" + accountId);
            metricsHelper.timer("redis.storage.timer", "delete_account", Instant.now().minus(startTime, ChronoUnit.MILLIS).toEpochMilli());
        } catch (Exception e) {
            log.error("[DELETE] Failed to delete accountId={}. ", accountId, e);
            return false;
        }
        log.info("[DELETE] removed account with id={}", accountId);
        return true;
    }

}
