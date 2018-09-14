package com.github.shimopus.revolutmoneyexchange.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(TransactionsController.BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsController {
    public static final String BASE_URL = "/transactions";

    /**
     *
     * @return
     */
    @GET
    public Response getAllTransactions() {
        return Response.ok("Yes, I can work").build();
    }
}
