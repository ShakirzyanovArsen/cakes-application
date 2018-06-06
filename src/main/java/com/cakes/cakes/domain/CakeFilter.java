package com.cakes.cakes.domain;

public class CakeFilter {
    private int page;
    private int limit;
    private String text;
    private StatusType statuses[];

    public CakeFilter() {
        this.page = 1;
        this.limit = 5;
        this.statuses = new StatusType[0];
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public StatusType[] getStatuses() {
        return statuses;
    }

    public void setStatuses(StatusType[] statuses) {
        this.statuses = statuses;
    }
}
