package com.github.shimopus.revolutmoneyexchange.exceptions;

public class ObjectModificationException extends Exception {
    public enum Type {
        OBJECT_IS_MALFORMED("The entity passed has been malformed"),
        OBJECT_IS_NOT_FOUND("The entity with provided ID has not been found"),
        COULD_NOT_OBTAIN_ID("The system could not generate ID for this entity. Creation is failed.");

        private String message;

        Type(String message) {
            this.message = message;
        }

        public String getMessage() {return message;}

        @Override
        public String toString() {
            return message;
        }
    }

    private Type type;

    public ObjectModificationException(Type exceptionType, Throwable cause) {
        super(exceptionType.getMessage(), cause);
        type = exceptionType;
    }

    public ObjectModificationException(Type exceptionType) {
        super(exceptionType.getMessage());
        type = exceptionType;
    }

    public ObjectModificationException(Type exceptionType, String message) {
        super(exceptionType.getMessage() + ": " + message);
        type = exceptionType;
    }

    public Type getType() {
        return type;
    }
}
