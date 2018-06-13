package com.cakes.cakes.exception;

public class CakeNotStaleableException extends RuntimeException {
    public CakeNotStaleableException() {
        super("You can't stale cake");
    }
}
