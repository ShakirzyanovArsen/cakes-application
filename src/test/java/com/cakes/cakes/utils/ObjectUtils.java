package com.cakes.cakes.utils;

import com.cakes.cakes.domain.*;
import org.springframework.lang.Nullable;

import java.util.List;

public class ObjectUtils {
    public static Cake createCake(@Nullable Long id, String name, StatusType status) {
        Cake cake = new Cake();
        cake.setId(id);
        cake.setName(name);
        cake.setStatus(status);
        return cake;
    }

    public static CakeFilter createCakeFilter(int limit, int page, StatusType[] statuses, String text) {
        CakeFilter cakeFilter = new CakeFilter();
        cakeFilter.setLimit(limit);
        cakeFilter.setPage(page);
        cakeFilter.setStatuses(statuses);
        cakeFilter.setText(text);
        return cakeFilter;
    }

    public static CakeView createCakeView(List<CakeDto> cakes, Long total) {
        CakeView cakeView = new CakeView();
        cakeView.setItems(cakes);
        cakeView.setTotal(total);
        return cakeView;
    }

    public static CakeDto createDtoByCake(Cake cake) {
        CakeDto cakeDto = new CakeDto();
        cakeDto.setId(cake.getId());
        cakeDto.setName(cake.getName());
        cakeDto.setStatus(cake.getStatus());
        return cakeDto;
    }

    public static CakeDto createCakeDto(Long id, String name, StatusType status) {
        CakeDto cakeDto = new CakeDto();
        cakeDto.setId(id);
        cakeDto.setName(name);
        cakeDto.setStatus(status);
        return cakeDto;
    }
}
