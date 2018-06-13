package com.cakes.cakes.web;

import com.cakes.cakes.domain.CakeDto;
import com.cakes.cakes.domain.CakeFilter;
import com.cakes.cakes.domain.CakeView;
import com.cakes.cakes.service.CakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
public class CakeController {

    private CakeService cakeService;

    @Autowired
    public CakeController(CakeService cakeService) {
        this.cakeService = cakeService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody CompletableFuture<CakeView> getView(CakeFilter cakeFilter) {
        return cakeService.getView(cakeFilter);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody CompletableFuture<CakeDto> getCake(@PathVariable Long id) {
        return cakeService.getItem(id);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody CompletableFuture<Boolean> saveCake(@RequestBody CakeDto cakeDto) {
        return CompletableFuture.supplyAsync(() -> {
            cakeService.saveItem(cakeDto);
            return true;
        });
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody CompletableFuture<Boolean> removeCake(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            cakeService.removeItem(id);
            return true;
        });
    }

}
