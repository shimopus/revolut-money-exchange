package com.github.shimopus.revolutmoneyexchange.model;

public class ApplicationException {
    private String type;
    private String name;
    private String message;

    public ApplicationException(ExceptionType exceptionType, String message) {
        this.type = exceptionType.name();
        this.name = exceptionType.getMessage();
        this.message = message;
    }

    public ApplicationException(String type, String name, String message) {
        this.type = type;
        this.name = name;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
