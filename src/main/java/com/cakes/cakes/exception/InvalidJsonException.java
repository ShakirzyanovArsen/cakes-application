package com.cakes.cakes.exception;

public class InvalidJsonException extends RuntimeException {
    public InvalidJsonException(Class clazz) {
        super("Can't convert json to " + clazz.getName() + " entity");
    }
}
