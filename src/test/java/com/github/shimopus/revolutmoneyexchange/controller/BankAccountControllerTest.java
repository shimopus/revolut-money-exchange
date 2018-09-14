package com.github.shimopus.revolutmoneyexchange.controller;

import com.github.shimopus.revolutmoneyexchange.MoneyExchangeApp;
import com.github.shimopus.revolutmoneyexchange.dto.BankAccountDto;
import com.github.shimopus.revolutmoneyexchange.model.BankAccount;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class BankAccountControllerTest {
    private static HttpServer server;
    private static WebTarget target;

    @BeforeClass
    public static void beforeAll() throws Exception {
        // start the server
        server = MoneyExchangeApp.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        target = c.target(MoneyExchangeApp.BASE_URI);
    }

    @AfterClass
    public static void afterAll() throws Exception {
        server.shutdownNow();
    }

    @Test
    public void testGetAllBankAccounts() {
        Response response = target.path(BankAccountsController.BASE_URL)
                .request().get();

        assertEquals(Response.Status.OK, response.getStatusInfo().toEnum());

        Collection<BankAccount> bankAccount = response.readEntity(new GenericType<Collection<BankAccount>>(){});

        assertEquals(bankAccount.size(), BankAccountDto.getInstance().getAllBankAccounts().size());
    }

    @Test
    public void testGetBankAccountById() {
        Response response = getById(BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID);

        Assert.assertEquals(Response.Status.OK, response.getStatusInfo().toEnum());
    }

    @Test
    public void testGetNullBankAccount() {
        Response response = getById(null);

        Assert.assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo().toEnum());
    }

    private Response getById(Long id) {
        return target.path(BankAccountsController.BASE_URL + "/" + BankAccountsController.GET_BANK_ACCOUNT_BY_ID_PATH)
                .resolveTemplate("id", id == null ? "null" : id)
                .request().get();
    }

    private Response post(BankAccount bankAccount) {
        return target.path(BankAccountsController.BASE_URL)
                .request().post(from(bankAccount));
    }

    private static Entity from(BankAccount bankAccount) {
        return Entity.entity(bankAccount, MediaType.valueOf(MediaType.APPLICATION_JSON));
    }
}
