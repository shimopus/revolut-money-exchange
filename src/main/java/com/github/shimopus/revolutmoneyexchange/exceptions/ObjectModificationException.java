package com.github.shimopus.revolutmoneyexchange.exceptions;

import com.github.shimopus.revolutmoneyexchange.model.ExceptionType;

public class ObjectModificationException extends Exception {
    private ExceptionType type;

    public ObjectModificationException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType.getMessage(), cause);
        type = exceptionType;
    }

    public ObjectModificationException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        type = exceptionType;
    }

    public ObjectModificationException(ExceptionType exceptionType, String message) {
        super(exceptionType.getMessage() + ": " + message);
        type = exceptionType;
    }

    public ExceptionType getType() {
        return type;
    }
}
