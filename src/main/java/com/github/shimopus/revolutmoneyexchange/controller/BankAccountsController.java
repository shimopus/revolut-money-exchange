package com.github.shimopus.revolutmoneyexchange.controller;

import com.github.shimopus.revolutmoneyexchange.exceptions.ImpossibleOperationExecution;
import com.github.shimopus.revolutmoneyexchange.model.BankAccount;
import com.github.shimopus.revolutmoneyexchange.service.BankAccountService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Collection;

@Path(BankAccountsController.BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
public class BankAccountsController {
    public static final String BASE_URL = "/bankAccounts";

    /**
     *
     * @return
     */
    @GET
    public Response getAllBankAccounts() {
        Collection<BankAccount> bankAccounts = null;

        try {
            bankAccounts = BankAccountService.getInstance().getAllBankAccounts();
        } catch (ImpossibleOperationExecution e) {
            Response.serverError().entity(e).build();
        }

        if (bankAccounts == null) {
            Response.noContent().build();
        }

        return Response.ok(bankAccounts).build();
    }

    /**
     *
     * @return
     */
    @GET
    @Path("{id}")
    public Response getBankAccountById(@PathParam("id") Long id) {
        BankAccount bankAccount = null;

        try {
            bankAccount = BankAccountService.getInstance().getBankAccountById(id);
        } catch (ImpossibleOperationExecution e) {
            return Response.serverError().entity(e).build();
        }

        if (bankAccount == null) {
            Response.noContent().build();
        }

        return Response.ok(bankAccount).build();
    }
}
