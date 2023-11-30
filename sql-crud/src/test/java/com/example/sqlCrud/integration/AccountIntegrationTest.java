package com.example.sqlCrud.integration;

import com.example.sqlCrud.controllers.AccountController;
import com.example.sqlCrud.model.Account;
import com.example.sqlCrud.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static com.example.sqlCrud.integration.TestConstants.BASE_ACCOUNT_URL_SUFFIX;
import static com.example.sqlCrud.integration.TestConstants.BASE_URL;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(value = "test")
public class AccountIntegrationTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountController accountController;

    private final Account testAccount = Account.builder()
            .accountId("123")
            .accountName("testAccount")
            .build();

    private Account toUpdateAccount;

    @Test
    public void saveRetrieveAndDeleteAccountSuccessfully() {
        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .post()
                .uri(BASE_ACCOUNT_URL_SUFFIX)
                .bodyValue(testAccount)
                .exchange()
                .expectBody(String.class)
                .isEqualTo(testAccount.getAccountId());

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .get()
                .uri(String.join("/", BASE_ACCOUNT_URL_SUFFIX,testAccount.getAccountId()))
                .exchange()
                .expectBody(Account.class)
                .isEqualTo(testAccount);

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .delete()
                .uri(String.join("/", BASE_ACCOUNT_URL_SUFFIX,testAccount.getAccountId()))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    public void deleteNonExistentAccountShouldReturn2xx() {
        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .delete()
                .uri(String.join("/", BASE_ACCOUNT_URL_SUFFIX, UUID.randomUUID().toString()))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    public void getNonExistentAccountShouldReturn404NotFoundStatus() {
        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .get()
                .uri(String.join("/", BASE_ACCOUNT_URL_SUFFIX, UUID.randomUUID().toString()))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void shouldUpdateExistentAccountSuccessfully() {
        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .post()
                .uri(BASE_ACCOUNT_URL_SUFFIX)
                .bodyValue(testAccount)
                .exchange()
                .expectBody(String.class)
                .isEqualTo(testAccount.getAccountId());

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .get()
                .uri(String.join("/", BASE_ACCOUNT_URL_SUFFIX, testAccount.getAccountId()))
                .exchange()
                .expectBody(Account.class)
                .isEqualTo(testAccount);

        toUpdateAccount = Account.builder()
                .accountId("123")
                .accountName("NewName")
                .build();

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .put()
                .uri(String.join("/", BASE_ACCOUNT_URL_SUFFIX, testAccount.getAccountId()))
                .bodyValue(toUpdateAccount)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .get()
                .uri(String.join("/", BASE_ACCOUNT_URL_SUFFIX, testAccount.getAccountId()))
                .exchange()
                .expectBody(Account.class)
                .isEqualTo(toUpdateAccount);
    }

    @Test
    public void shouldCreateNewAccountWhenUpdateNonExistentAccount() {
        toUpdateAccount = Account.builder()
                .accountId("123")
                .accountName("NewName")
                .build();

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .put()
                .uri(String.join("/", BASE_ACCOUNT_URL_SUFFIX, testAccount.getAccountId()))
                .bodyValue(toUpdateAccount)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        WebTestClient
                .bindToServer()
                .baseUrl(BASE_URL)
                .build()
                .get()
                .uri(String.join("/", BASE_ACCOUNT_URL_SUFFIX, testAccount.getAccountId()))
                .exchange()
                .expectBody(Account.class)
                .isEqualTo(toUpdateAccount);
    }
}
