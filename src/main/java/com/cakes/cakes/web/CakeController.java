package com.cakes.cakes.web;

import com.cakes.cakes.domain.CakeDto;
import com.cakes.cakes.domain.CakeFilter;
import com.cakes.cakes.domain.CakeView;
import com.cakes.cakes.exception.InvalidJsonException;
import com.cakes.cakes.service.CakeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
public class CakeController {

    private CakeService cakeService;

    @Autowired
    public CakeController(CakeService cakeService) {
        this.cakeService = cakeService;
    }

    @RequestMapping(value = "/api/cakes", method = RequestMethod.GET)
    public @ResponseBody CompletableFuture<CakeView> getView(
            @RequestParam(value = "cakeFilter", required = false) String filter) {
        ObjectMapper mapper = new ObjectMapper();
        CakeFilter cakeFilter;
        if(filter != null) {
            try {
                cakeFilter = mapper.readValue(filter, CakeFilter.class);
            } catch (IOException e) {
                throw new InvalidJsonException(CakeFilter.class);
            }
        } else {
            cakeFilter = new CakeFilter();
        }
        return cakeService.getView(cakeFilter);
    }

    @RequestMapping(value = "/api/cakes/{id}", method = RequestMethod.GET)
    public @ResponseBody CompletableFuture<CakeDto> getCake(@PathVariable Long id) {
        return cakeService.getItem(id);
    }

    @RequestMapping(value = "/api/cakes", method = RequestMethod.POST)
    public @ResponseBody CompletableFuture<Boolean> saveCake(@RequestBody CakeDto cakeDto) {
        return CompletableFuture.supplyAsync(() -> {
            cakeService.saveItem(cakeDto).join();
            return true;
        });
    }

    @RequestMapping(value = "/api/cakes/{id}", method = RequestMethod.DELETE)
    public @ResponseBody CompletableFuture<Boolean> removeCake(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            cakeService.removeItem(id).join();
            return true;
        });
    }

}
