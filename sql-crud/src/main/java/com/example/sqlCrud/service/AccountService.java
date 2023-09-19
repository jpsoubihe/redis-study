package com.example.sqlCrud.service;

import com.example.common.exceptions.AccountNotFoundException;
import com.example.sqlCrud.model.Account;
import com.example.sqlCrud.repositories.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AccountService {


    @Autowired
    private AccountRepository accountRepository;

    public String createAccount(Account account) {
        log.info("[POST] Starting account creation with id={}", account.getAccountId());
        Account savedAccount = saveAccount(account);
        log.info("[POST] Successfully created account with id={}", savedAccount.getAccountId());
        return savedAccount.getAccountId();
    }

    public Account getAccount(String accountId) throws AccountNotFoundException {
        log.info("[GET] Retrieveing account with id={}", accountId);
        return accountRepository.findAccountByAccountId(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(String.format("Account with id=%s was not found.", accountId)));
    }

    public boolean updateAccount(String accountId, Account account) {
        log.info("[PUT] Starting update of account with id={}", accountId);
        accountRepository.findAccountByAccountId(accountId)
                .ifPresentOrElse(retrievedAccount ->
                        accountRepository.save(updateAccountInformation(retrievedAccount, account)),
                        () -> accountRepository.save(saveAccount(account)));
        return true;
    }

    public boolean deleteAccount(String accountId) {
        log.info("[DELETE] Starting deletion of account with id={}", accountId);
        try {
            accountRepository.deleteById(accountId);
        } catch (Exception ex) {
            log.error("[DELETE] Failed deletion of account with id={}. Cause:{}", accountId, ex);
            return false;
        }
        return true;
    }

    private Account saveAccount(Account account) {
        if(account.getAccountId().isEmpty()) {
            account.setAccountId(UUID.randomUUID().toString());
        }
        return accountRepository.save(account);
    }

    private Account updateAccountInformation(Account accountRecord, Account newAccount) {
        log.info("Complementing info for account record of id={}", accountRecord.getAccountId());
        Optional.ofNullable(newAccount.getAccountId()).ifPresent(accountRecord::setAccountId);
        Optional.ofNullable(newAccount.getAccountName()).ifPresent(accountRecord::setAccountName);
        return accountRecord;
    }
}
