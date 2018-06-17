package com.cakes.cakes.domain;

public enum StatusType {
    fresh("fresh"), stale("stale");

    private final String value;

    private StatusType(String value) {
        this.value=value;
    }

    public String value() {
        return this.value;
    }
}
