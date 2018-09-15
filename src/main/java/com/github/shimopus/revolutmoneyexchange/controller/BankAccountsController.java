package com.github.shimopus.revolutmoneyexchange.controller;

import com.github.shimopus.revolutmoneyexchange.exceptions.ImpossibleOperationExecution;
import com.github.shimopus.revolutmoneyexchange.exceptions.ObjectModificationException;
import com.github.shimopus.revolutmoneyexchange.model.BankAccount;
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
    @Path("{" + GET_BANK_ACCOUNT_BY_ID_PATH + "}")
    public Response getBankAccountById(@PathParam(GET_BANK_ACCOUNT_BY_ID_PATH) Long id) {
        BankAccount bankAccount = null;

        try {
            bankAccount = BankAccountService.getInstance().getBankAccountById(id);
        } catch (ImpossibleOperationExecution e) {
            return Response.serverError().entity(e).build();
        }

        if (bankAccount == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(bankAccount).build();
    }

    /**
     *
     * @return
     */
    @PUT
    public Response updateBankAccount(BankAccount bankAccount) {
        try {
            BankAccountService.getInstance().updateBankAccount(bankAccount);
        } catch (ObjectModificationException e) {
            return unwrapObjectUpdateException(e);
        }

        return Response.ok(bankAccount).build();
    }

    /**
     *
     * @return
     */
    @POST
    public Response createBankAccount(BankAccount bankAccount) {
        BankAccount createdBankAccount;

        try {
            createdBankAccount = BankAccountService.getInstance().createBankAccount(bankAccount);
        } catch (ObjectModificationException e) {
            return unwrapObjectUpdateException(e);
        }

        return Response.ok(createdBankAccount).build();
    }

    private Response unwrapObjectUpdateException(ObjectModificationException exception) {
        log.error(exception.getMessage(), exception);

        ObjectModificationException.Type type = exception.getType();

        if (type == ObjectModificationException.Type.OBJECT_IS_NOT_FOUND) {
            return Response.status(Response.Status.NOT_FOUND).entity(type.getMessage()).build();
        }
        if (type == ObjectModificationException.Type.OBJECT_IS_MALFORMED) {
            return Response.serverError().entity(type.getMessage()).build();
        }

        return Response.serverError().build();
    }
}
