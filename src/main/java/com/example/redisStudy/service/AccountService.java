package com.example.redisStudy.service;

import com.example.redisStudy.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.management.AttributeNotFoundException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AccountService {

    @Autowired
    RedisTemplate<String, Account> redisAccountTemplate;

    public String createAccount(Account account) {
        if(account.getAccountId() == null) {
            account.setAccountId(UUID.randomUUID().toString());
        }

        redisAccountTemplate.opsForValue().set("accounts:" + account.getAccountId(), account, 1, TimeUnit.DAYS);

        log.info("[POST] saved account with id={}", account.getAccountId());

        return account.getAccountId();
    }

    public Account getAccount(String accountId) throws AttributeNotFoundException {
        log.info("[GET] retrieving account with id={}", accountId);
        return Optional.ofNullable(
                redisAccountTemplate.opsForValue().get("accounts:" + accountId))
                .filter(Objects::nonNull)
                .orElseThrow(AttributeNotFoundException::new);

    }

    public boolean updateAccount(String accountId, Account newAccount) {
        log.info("[PUT] updating account with id={}", accountId);

        newAccount.setAccountId(accountId);
        try {
            redisAccountTemplate.opsForValue().set("accounts:" + newAccount.getAccountId(), newAccount, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("[PUT] Failed to update accountId={}. ", accountId, e);
            return false;
        }
        return true;
    }

    public boolean deleteAccount(String accountId) {
        try {
            redisAccountTemplate.delete("accounts:" + accountId);
        } catch (Exception e) {
            log.error("[DELETE] Failed to delete accountId={}. ", accountId, e);
            return false;
        }
        log.info("[DELETE] removed account with id={}", accountId);
        return true;
    }

}
