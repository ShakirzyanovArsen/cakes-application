package com.cakes.cakes.service;

import com.cakes.cakes.domain.*;
import com.cakes.cakes.exception.CakeNotStaleableException;
import com.cakes.cakes.exception.EntityNotFoundException;
import com.cakes.cakes.repository.CakeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DefaultCakeService implements CakeService {

    private final CakeRepository cakeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public DefaultCakeService(CakeRepository cakeRepository, ModelMapper modelMapper) {
        this.cakeRepository = cakeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<CakeDto> getItem(Long id) {
        CompletableFuture<Cake> cakeFuture = cakeRepository.getItem(id);
        return cakeFuture.thenApply(cake -> {
            if (cake == null) {
                throw new EntityNotFoundException(Cake.class);
            }
            return modelMapper.map(cake, CakeDto.class);
        });
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<CakeView> getView(CakeFilter filter) {
        CompletableFuture<List<Cake>> rangeFuture = cakeRepository.getRange(filter);
        CompletableFuture<Long> totalFuture = cakeRepository.getTotal(filter);
        return totalFuture.thenCombine(rangeFuture, (Long total, List<Cake> range) -> {
            CakeView cakeView = new CakeView();
            List<CakeDto> dtoList = range.stream().map(cake -> modelMapper.map(cake, CakeDto.class))
                    .collect(Collectors.toList());
            cakeView.setItems(dtoList);
            cakeView.setTotal(total);
            return cakeView;
        });
    }

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> saveItem(CakeDto item) {
        Cake cake = modelMapper.map(item, Cake.class);
        if(cake.getId() != null) {
            Cake cakeInDb = cakeRepository.getItem(cake.getId()).join();
            if(cakeInDb == null) {
                throw new EntityNotFoundException(Cake.class);
            }
            if(cakeInDb.getStatus() != StatusType.stale && cake.getStatus() == StatusType.stale) {
                throw new CakeNotStaleableException();
            }
            return cakeRepository.updateItem(cake);
        } else {
            return cakeRepository.addItem(cake);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> removeItem(Long id) {
        CompletableFuture<Cake> cakeFuture = cakeRepository.getItem(id);
        return cakeFuture.thenApply(cake -> {
            if(cake == null) {
                throw new EntityNotFoundException(Cake.class);
            }
            cakeRepository.removeItem(cake);
            return null;
        });
    }

    @Override
    @Async
    public CompletableFuture<Long> getTotal(CakeFilter filter) {
        return cakeRepository.getTotal(filter);
    }
}
