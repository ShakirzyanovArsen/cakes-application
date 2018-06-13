package com.cakes.cakes.service;

import com.cakes.cakes.domain.Cake;
import com.cakes.cakes.domain.CakeDto;
import com.cakes.cakes.domain.CakeFilter;
import com.cakes.cakes.domain.CakeView;
import com.cakes.cakes.exception.EntityNotFoundException;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface CakeService {
    CompletableFuture<CakeDto> getItem(Long id);
    CompletableFuture<CakeView> getView(CakeFilter filter);
    CompletableFuture<Void> saveItem(CakeDto item);
    CompletableFuture<Void> removeItem(Long id);
    CompletableFuture<Long> getTotal(CakeFilter filter);
}
