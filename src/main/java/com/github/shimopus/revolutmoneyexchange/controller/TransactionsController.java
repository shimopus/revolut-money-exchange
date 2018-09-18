package com.github.shimopus.revolutmoneyexchange.controller;

import com.github.shimopus.revolutmoneyexchange.exceptions.ObjectModificationException;
import com.github.shimopus.revolutmoneyexchange.model.Transaction;
import com.github.shimopus.revolutmoneyexchange.service.ConstantMoneyExchangeService;
import com.github.shimopus.revolutmoneyexchange.service.TransactionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The resource is responsible for the Transaction entity. Make it possible to create,
 * update of transactions. Also there is an ability to execute created transaction
 */
@Path(TransactionsController.BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsController {
    private final Logger log = LoggerFactory.getLogger(TransactionsController.class);

    public static final String BASE_URL = "/transactions";
    public static final String GET_TRANSACTION_BY_ID_PATH = "id";

    private TransactionsService transactionsService = TransactionsService.getInstance(new ConstantMoneyExchangeService());

    /**
     * Returns all transactions in the system with there statuses
     *
     * @return TODO: multi paging should be implemented
     */
    @GET
    public Response getAllTransactions() {
        return Response.ok().entity(transactionsService.getAllTransactions()).build();
    }

    /**
     * Returns specific transaction by ID
     *
     * @return
     */
    @GET()
    @Path("{" + GET_TRANSACTION_BY_ID_PATH + "}")
    public Response getTransactionById(@PathParam(GET_TRANSACTION_BY_ID_PATH) Long id) {
        return Response.ok().entity(transactionsService.getTransactionById(id)).build();
    }

    /**
     * Make it possible to create money transfer from one account to another.
     * The result of execution is created transaction with actual status. Usually it is "IN PROGRESS"
     *
     * @return
     */
    @POST()
    public Response createTransaction(Transaction transaction) throws ObjectModificationException {
        transaction = transactionsService.createTransaction(transaction);

        return Response.ok().entity(transaction).build();
    }
}
