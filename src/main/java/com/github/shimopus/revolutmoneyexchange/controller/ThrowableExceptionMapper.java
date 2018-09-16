package com.github.shimopus.revolutmoneyexchange.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger log = LoggerFactory.getLogger(ThrowableExceptionMapper.class);

    @Override
    public Response toResponse(Throwable t) {
        log.error("Uncaught exception", t);
        return Response.serverError().entity("Your request could not been processed. Please contact" +
                " to Administrator").build();
    }
}