package com.github.shimopus.revolutmoneyexchange.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The resource is responsible for the Transaction entity. Make it possible to create,
 * update of transactions. Also there is an ability to execute created transaction
 *
 */
@Path(TransactionsController.BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsController {
    public static final String BASE_URL = "/transactions";

    /**
     * Returns all the transactions in the system with there statuses
     *
     * @return
     *
     * TODO: multi paging should be implemented
     */
    @GET
    public Response getAllTransactions() {
        return Response.ok("Yes, I can work").build();
    }
}
