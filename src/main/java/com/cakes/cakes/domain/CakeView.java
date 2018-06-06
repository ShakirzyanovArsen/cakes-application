package com.cakes.cakes.domain;

import java.util.List;

public class CakeView {
    List<CakeDto> items;
    Long total;

    public CakeView() {
    }

    public List<CakeDto> getItems() {
        return items;
    }

    public void setItems(List<CakeDto> items) {
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
