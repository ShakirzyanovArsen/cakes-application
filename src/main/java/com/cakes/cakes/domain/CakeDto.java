package com.cakes.cakes.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

public class CakeDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private StatusType status;

    public CakeDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }
}
