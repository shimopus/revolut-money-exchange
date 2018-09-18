package com.github.shimopus.revolutmoneyexchange.controller;

import com.github.shimopus.revolutmoneyexchange.exceptions.ObjectModificationException;
import com.github.shimopus.revolutmoneyexchange.model.ApplicationException;
import com.github.shimopus.revolutmoneyexchange.model.ExceptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger log = LoggerFactory.getLogger(ThrowableExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        ApplicationException applicationException;
        Response.ResponseBuilder serverError = Response.serverError();

        if (exception instanceof WebApplicationException) {
            applicationException = new ApplicationException(ExceptionType.UNEXPECTED_EXCEPTION.name(),
                    computeExceptionMessage(((WebApplicationException) exception).getResponse()),
                    exception.getMessage());
            serverError = serverError.status(((WebApplicationException) exception).getResponse().getStatus());
        } else if (exception instanceof ObjectModificationException) {
            ExceptionType type = ((ObjectModificationException) exception).getType();

            if (type == ExceptionType.OBJECT_IS_NOT_FOUND) {
                serverError = serverError.status(Response.Status.NOT_FOUND);
            }
            if (type == ExceptionType.OBJECT_IS_MALFORMED) {
                serverError = serverError.status(Response.Status.INTERNAL_SERVER_ERROR);;
            }
            applicationException = new ApplicationException(type, exception.getMessage());
        } else {
            applicationException = new ApplicationException(ExceptionType.UNEXPECTED_EXCEPTION,
                    exception.getMessage());
        }

        log.error("Uncaught exception", exception);
        return serverError.entity(applicationException).build();
    }

    private static String computeExceptionMessage(Response response) {
        Object statusInfo;
        if (response != null) {
            statusInfo = response.getStatusInfo();
        } else {
            statusInfo = Response.Status.INTERNAL_SERVER_ERROR;
        }

        return "HTTP " + ((Response.StatusType)statusInfo).getStatusCode() + ' ' + ((Response.StatusType)statusInfo).getReasonPhrase();
    }
}