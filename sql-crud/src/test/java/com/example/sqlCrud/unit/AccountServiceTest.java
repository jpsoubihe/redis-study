package com.example.sqlCrud.unit;

import com.example.common.exceptions.AccountNotFoundException;
import com.example.sqlCrud.metrics.MetricsHelper;
import com.example.sqlCrud.model.Account;
import com.example.sqlCrud.repositories.AccountRepository;
import com.example.sqlCrud.service.AccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    AccountService accountService;

    @Mock
    AccountRepository accountRepository;

    @Mock
    MetricsHelper metricsHelper;

    protected ArgumentCaptor<Account> argumentCaptor = ArgumentCaptor.forClass(Account.class);

    private String accountId;

    @BeforeEach
    public void setUp() {
        accountId = UUID.randomUUID().toString();
    }

    @Test
    public void shouldSuccessfullyCreateAccount() {
        String accountName = "Test Account";
        Account account = Account.builder()
                .accountId(accountId)
                .accountName(accountName)
                .build();

        when(accountRepository.save(any())).thenReturn(account);
        String savedAccountId = accountService.createAccount(account);
        Assertions.assertEquals(accountId, savedAccountId);
    }

    @Test
    public void shouldSuccessfullyCreateAccountIfNoAccountIdSpecified() {
        String accountName = "Test Account";
        Account account = Account.builder()
                .accountName(accountName)
                .build();

        when(accountRepository.save(argumentCaptor.capture())).thenReturn(account);
        accountService.createAccount(account);
        Account savedAccount = argumentCaptor.getValue();
        Assertions.assertNotNull(savedAccount);
        Assertions.assertEquals(accountName, savedAccount.getAccountName());
        Assertions.assertNotNull(savedAccount.getAccountId());
    }

    @Test
    public void shouldSuccessfullyRetrieveAccount() throws AccountNotFoundException {
        String accountName = "Test Account";
        Account account = Account.builder()
                .accountId(accountId)
                .accountName(accountName)
                .build();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        Account retrievedAccount = accountService.getAccount(accountId);
        Assertions.assertEquals(account, retrievedAccount);
    }

    @Test
    public void shouldThrowAccountNotFoundExceptionIfNoAccountIsFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        Assertions.assertThrows(AccountNotFoundException.class, () -> accountService.getAccount(accountId));
    }

    @Test
    public void shouldSuccessfullyUpdateAccountIfAccountPresent() {
        String accountName = "Test Account";
        String updatedName = "Updated Test Account";
        Account account = Account.builder()
                .accountId(accountId)
                .accountName(accountName)
                .build();
        Account updateAccount = Account.builder()
                .accountId(accountId)
                .accountName(updatedName)
                .build();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(updateAccount)).thenReturn(updateAccount);

        Assertions.assertTrue(accountService.updateAccount(accountId, updateAccount));
        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(updateAccount);
    }

    @Test
    public void shouldSuccessfullyUpdateAccountIfAccountNotPresent() {
        String updatedName = "Updated Test Account";
        Account updateAccount = Account.builder()
                .accountId(accountId)
                .accountName(updatedName)
                .build();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        when(accountRepository.save(updateAccount)).thenReturn(updateAccount);

        Assertions.assertTrue(accountService.updateAccount(accountId, updateAccount));
        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(updateAccount);
    }

    @Test
    public void shouldSuccessfullyDeleteAccount() {
        Assertions.assertTrue(accountService.deleteAccount(accountId));
    }

    @Test
    public void shouldReturnFalseWhenDeleteAccountFails() {
        doThrow(RuntimeException.class).when(accountRepository).deleteById(accountId);
        Assertions.assertFalse(accountService.deleteAccount(accountId));
    }
}
