package com.example.redisCrud.unit.service;

import com.example.redisCrud.exceptions.AccountNotFoundException;
import com.example.redisCrud.model.Account;
import com.example.redisCrud.service.AccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    AccountService accountService;

    @Mock
    RedisTemplate<String, Account> accountRedisTemplate;

    @Mock
    private ValueOperations<String, Account> valueOperations;

    private String accountId;

    @BeforeEach
    public void setup() {
        accountId = UUID.randomUUID().toString();
    }

    @Test
    public void shouldSuccessfullyCreateAccount() {
        when(accountRedisTemplate.opsForValue()).thenReturn(valueOperations);
        Account account = createStandardTestAccount(accountId);

        doNothing().when(valueOperations).set(anyString(), any(Account.class), anyLong(), any());

        String createdAccountId = accountService.createAccount(account);
        Assertions.assertEquals(accountId, createdAccountId);
    }

    @Test
    public void shouldSuccessfullyCreateAccountWithNewId() {
        when(accountRedisTemplate.opsForValue()).thenReturn(valueOperations);
        Account account = createStandardTestAccount(null);

        doNothing().when(valueOperations).set(anyString(), any(Account.class), anyLong(), any());

        String createdAccountId = accountService.createAccount(account);
        Assertions.assertNotNull(createdAccountId);
    }

    @Test
    public void shouldSuccessfullyGetAccount() throws AccountNotFoundException {
        when(accountRedisTemplate.opsForValue()).thenReturn(valueOperations);
        Account account = createStandardTestAccount(accountId);

        when(valueOperations.get("accounts:" + accountId))
                .thenReturn(account);

        Account retrievedAccount = accountService.getAccount(accountId);
        Assertions.assertEquals(account, retrievedAccount);
    }

    @Test
    public void shouldThrowExceptionIfElementNotFoundWhenGetAccount() {
        when(accountRedisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get("accounts:" + accountId))
                .thenReturn(null);

        Assertions.assertThrows(AccountNotFoundException.class, () -> accountService.getAccount(accountId));
    }

    @Test
    public void shouldReturnTrueIfAccountSuccessfullyUpdated() {
        when(accountRedisTemplate.opsForValue()).thenReturn(valueOperations);
        Account account = createStandardTestAccount(accountId);

        doNothing().when(valueOperations).set(anyString(), any(Account.class), anyLong(), any());

        Assertions.assertTrue(accountService.updateAccount(accountId, account));
    }

    @Test
    public void shouldReturnFalseIfAccountUpdateFailed() {
        when(accountRedisTemplate.opsForValue()).thenReturn(valueOperations);
        Account account = createStandardTestAccount(accountId);

        doThrow(RuntimeException.class).when(valueOperations)
                .set(anyString(), any(Account.class), anyLong(), any());

        Assertions.assertFalse(accountService.updateAccount(accountId, account));
    }

    @Test
    public void shouldReturnTrueIfSuccessfullyDeleteAccount() {
        when(accountRedisTemplate.delete("accounts:" + accountId)).thenReturn(true);
        Assertions.assertTrue(accountService.deleteAccount(accountId));
    }

    @Test
    public void shouldReturnFalseIfDeleteAccountFailed() {
        when(accountRedisTemplate.delete("accounts:" + accountId)).thenThrow(RuntimeException.class);
        Assertions.assertFalse(accountService.deleteAccount(accountId));
    }

    private Account createStandardTestAccount(String createdAccountId) {
        return Account.builder()
                .accountId(createdAccountId)
                .accountName("TestAccount")
                .build();
    }
}