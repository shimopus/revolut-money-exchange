package com.github.shimopus.revolutmoneyexchange.controller;

import com.github.shimopus.revolutmoneyexchange.exceptions.ImpossibleOperationExecution;
import com.github.shimopus.revolutmoneyexchange.exceptions.ObjectModificationException;
import com.github.shimopus.revolutmoneyexchange.model.BankAccount;
import com.github.shimopus.revolutmoneyexchange.model.ExceptionType;
import com.github.shimopus.revolutmoneyexchange.service.BankAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path(BankAccountsController.BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
public class BankAccountsController {
    private final Logger log = LoggerFactory.getLogger(BankAccountsController.class);

    public static final String BASE_URL = "/bankAccounts";
    public static final String GET_BANK_ACCOUNT_BY_ID_PATH = "id";

    /**
     * @return
     */
    @GET
    public Response getAllBankAccounts() {
        Collection<BankAccount> bankAccounts = null;

        bankAccounts = BankAccountService.getInstance().getAllBankAccounts();

        if (bankAccounts == null) {
            Response.noContent().build();
        }

        return Response.ok(bankAccounts).build();
    }

    /**
     * @return
     */
    @GET
    @Path("{" + GET_BANK_ACCOUNT_BY_ID_PATH + "}")
    public Response getBankAccountById(@PathParam(GET_BANK_ACCOUNT_BY_ID_PATH) Long id) {
        BankAccount bankAccount;


        bankAccount = BankAccountService.getInstance().getBankAccountById(id);

        if (bankAccount == null) {
            throw new WebApplicationException("The bank account is not exists", Response.Status.NOT_FOUND);
        }

        return Response.ok(bankAccount).build();
    }

    /**
     * @return
     */
    @PUT
    public Response updateBankAccount(BankAccount bankAccount) throws ObjectModificationException {
        BankAccountService.getInstance().updateBankAccount(bankAccount);

        return Response.ok(bankAccount).build();
    }

    /**
     * @return
     */
    @POST
    public Response createBankAccount(BankAccount bankAccount) throws ObjectModificationException {
        BankAccount createdBankAccount;

        createdBankAccount = BankAccountService.getInstance().createBankAccount(bankAccount);

        return Response.ok(createdBankAccount).build();
    }
}
