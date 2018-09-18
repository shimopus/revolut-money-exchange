package com.github.shimopus.revolutmoneyexchange.integration;

import com.github.shimopus.revolutmoneyexchange.MoneyExchangeApp;
import com.github.shimopus.revolutmoneyexchange.controller.BankAccountsController;
import com.github.shimopus.revolutmoneyexchange.dto.BankAccountDto;
import com.github.shimopus.revolutmoneyexchange.model.BankAccount;
import com.github.shimopus.revolutmoneyexchange.model.Currency;
import com.github.shimopus.revolutmoneyexchange.service.BankAccountService;
import org.glassfish.grizzly.http.server.HttpServer;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.Assert.assertNotEquals;

public class BankAccountControllerTest {
    private static HttpServer server;
    private static WebTarget target;

    @BeforeClass
    public static void beforeAll() {
        // start the server
        server = MoneyExchangeApp.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        target = c.target(MoneyExchangeApp.BASE_URI);
    }

    @AfterClass
    public static void afterAll() {
        server.shutdownNow();
    }

    /**
     * Tests that all bank accounts will be returned from the database
     */
    @Test
    public void testGetAllBankAccounts() {
        Response response = target.path(BankAccountsController.BASE_URL)
                .request().get();

        assertEquals(Response.Status.OK, response.getStatusInfo().toEnum());

        Collection<BankAccount> bankAccount = response.readEntity(new GenericType<Collection<BankAccount>>(){});

        assertEquals(bankAccount.size(), BankAccountDto.getInstance().getAllBankAccounts().size());
    }

    /**
     * Tests that particular bank account will be returned from the database
     */
    @Test
    public void testGetBankAccountById() {
        Response response = getById(BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID);

        assertEquals(Response.Status.OK, response.getStatusInfo().toEnum());

        BankAccount bankAccount = response.readEntity(BankAccount.class);

        assertEquals(bankAccount.getId(), BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID);
    }

    /**
     * Tests that method responds correctly if ID will be passed incorrectly (non int)
     */
    @Test
    public void testGetNullBankAccount() {
        Response response = getById(null);

        assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo().toEnum());
    }

    /**
     * Tests that non existing bank account will be returned correctly
     */
    @Test
    public void testNonExistingBankAccountById() {
        Response response = getById(new Random().nextLong());

        assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo().toEnum());
    }

    /**
     * Tests the successful update of the bank account
     */
    @Test
    public void testUpdateBankAccount() {
        BankAccountService bankAccountService = BankAccountService.getInstance();
        BigDecimal BLOCKED_AMOUNT = new BigDecimal(23);

        BankAccount secondAccount = bankAccountService.getBankAccountById(BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID);
        secondAccount.setBlockedAmount(BLOCKED_AMOUNT);

        Response response = target.path(BankAccountsController.BASE_URL)
                .request()
                .put(from(secondAccount));

        assertEquals(Response.Status.OK, response.getStatusInfo().toEnum());

        BankAccount updatedAccount = bankAccountService.getBankAccountById(BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID);

        assertThat(BLOCKED_AMOUNT, Matchers.comparesEqualTo(updatedAccount.getBlockedAmount()));
    }

    /**
     * Tests the unsuccessful update of the bank account with non-existing id
     */
    @Test
    public void testUpdateNonExistingBankAccount() {
        BankAccount bankAccount = new BankAccount(new Random().nextLong(),
                "", BigDecimal.ZERO, BigDecimal.ZERO, Currency.RUB);

        Response response = target.path(BankAccountsController.BASE_URL)
                .request()
                .put(from(bankAccount));

        assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo().toEnum());
    }

    /**
     * Tests the unsuccessful update of the incorrect bank account
     */
    @Test
    public void testIncorrectUpdateBankAccount() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(new Random().nextLong());

        Response response = target.path(BankAccountsController.BASE_URL)
                .request()
                .put(from(bankAccount));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo().toEnum());
    }

    /**
     * Tests the successful creation of the new bank account
     */
    @Test
    public void testCreateBankAccount() {
        BankAccountService bankAccountService = BankAccountService.getInstance();
        String OWNER_NAME = "Sergio";

        BankAccount bankAccount = new BankAccount(OWNER_NAME, BigDecimal.ZERO, BigDecimal.ZERO, Currency.RUB);

        Response response = target.path(BankAccountsController.BASE_URL)
                .request()
                .post(from(bankAccount));

        assertEquals(Response.Status.OK, response.getStatusInfo().toEnum());

        BankAccount returnedAccount = response.readEntity(BankAccount.class);
        BankAccount createdAccount = bankAccountService.getBankAccountById(returnedAccount.getId());

        assertNotNull(returnedAccount);
        assertNotNull(createdAccount);

        assertNotEquals(returnedAccount.getId(), bankAccount.getId());
        assertEquals(returnedAccount.getId(), createdAccount.getId());
        assertEquals(OWNER_NAME, createdAccount.getOwnerName());
    }

    private Response getById(Long id) {
        return target.path(BankAccountsController.BASE_URL + "/{" + BankAccountsController.GET_BANK_ACCOUNT_BY_ID_PATH + "}")
                .resolveTemplate("id", id == null ? "null" : id)
                .request().get();
    }

    private static Entity from(BankAccount bankAccount) {
        return Entity.entity(bankAccount, MediaType.valueOf(MediaType.APPLICATION_JSON));
    }
}
