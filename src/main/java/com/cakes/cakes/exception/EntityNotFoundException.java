package com.cakes.cakes.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class clazz) {
        super(String.format("Entity %s not found.", clazz.getName()));
    }

    public EntityNotFoundException(Class clazz, Throwable t) {
        super(String.format("Entity %s not found.", clazz.getName()), t);
    }
}
