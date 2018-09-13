package com.github.shimopus.revolutmoneyexchange.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(TransactionController.BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionController {
    public static final String BASE_URL = "/transactions";

    /**
     *
     * @return
     */
    @GET
    public String getAllTransactions() {
        return "Yes, I can work";
    }
}
