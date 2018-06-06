package com.cakes.cakes.repository;

import com.cakes.cakes.domain.Cake;
import com.cakes.cakes.domain.CakeFilter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public interface CakeRepository {
    CompletableFuture<Cake> getItem(Long id);
    CompletableFuture<List<Cake>> getRange(CakeFilter filter);
    CompletableFuture<Void> addItem(Cake item);
    CompletableFuture<Void> updateItem(Cake item);
    CompletableFuture<Void> removeItem(Cake item);
    CompletableFuture<Long> getTotal(CakeFilter filter);
}
