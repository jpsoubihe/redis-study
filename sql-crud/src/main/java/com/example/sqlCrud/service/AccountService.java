package com.example.sqlCrud.service;

import com.example.common.exceptions.AccountNotFoundException;
import com.example.sqlCrud.metrics.MetricsHelper;
import com.example.sqlCrud.model.Account;
import com.example.sqlCrud.repositories.AccountRepository;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AccountService {

    private AccountRepository accountRepository;
    private MetricsHelper metricsHelper;

    public AccountService(AccountRepository accountRepository, MetricsHelper metricsHelper) {
        this.accountRepository = accountRepository;
        this.metricsHelper = metricsHelper;
    }

    public String createAccount(Account account) {
        log.info("[POST] Starting account creation with id={}", account.getAccountId());
        Account savedAccount = saveAccount(account);
        log.info("[POST] Successfully created account with id={}", savedAccount.getAccountId());
        return savedAccount.getAccountId();
    }

    public Account getAccount(String accountId) throws AccountNotFoundException {
        log.info("[GET] Retrieveing account with id={}", accountId);
        long startTime = Instant.now().toEpochMilli();
        Account retrievedAccount =  accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(String.format("Account with id=%s was not found.", accountId)));
        metricsHelper.timer("sql.storage.timer", "get_account", Instant.now().minus(startTime, ChronoUnit.MILLIS).toEpochMilli());
        return retrievedAccount;
    }

    public boolean updateAccount(String accountId, Account account) {
        log.info("[PUT] Starting update of account with id={}", accountId);
        long startTime = Instant.now().toEpochMilli();
        accountRepository.findById(accountId)
                .ifPresentOrElse(retrievedAccount ->
                        saveAccount(updateAccountInformation(retrievedAccount, account)),
                        () -> saveAccount(account));
        metricsHelper.timer("sql.storage.timer", "update_account", Instant.now().minus(startTime, ChronoUnit.MILLIS).toEpochMilli());
        return true;
    }

    public boolean deleteAccount(String accountId) {
        log.info("[DELETE] Starting deletion of account with id={}", accountId);
        long startTime = Instant.now().toEpochMilli();
        try {
            accountRepository.deleteById(accountId);
        } catch (Exception ex) {
            log.error("[DELETE] Failed deletion of account with id={}. Cause:{}", accountId, ex);
            return false;
        }
        metricsHelper.timer("sql.storage.timer", "delete_account", Instant.now().minus(startTime, ChronoUnit.MILLIS).toEpochMilli());
        return true;
    }

    private Account saveAccount(Account account) {
        if(StringUtils.isEmpty(account.getAccountId())) {
            account.setAccountId(UUID.randomUUID().toString());
        }
        long startTime = Instant.now().toEpochMilli();
        Account createdAccount  = accountRepository.save(account);
        metricsHelper.timer("sql.storage.timer", "create_account", Instant.now().minus(startTime, ChronoUnit.MILLIS).toEpochMilli());
        return createdAccount;
    }

    private Account updateAccountInformation(Account accountRecord, Account newAccount) {
        log.info("Complementing info for account record of id={}", accountRecord.getAccountId());
        Optional.ofNullable(newAccount.getAccountId()).ifPresent(accountRecord::setAccountId);
        Optional.ofNullable(newAccount.getAccountName()).ifPresent(accountRecord::setAccountName);
        return accountRecord;
    }
}
