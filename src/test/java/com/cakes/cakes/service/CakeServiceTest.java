package com.cakes.cakes.service;

import com.cakes.cakes.domain.Cake;
import com.cakes.cakes.domain.CakeDto;
import com.cakes.cakes.domain.StatusType;
import com.cakes.cakes.exception.EntityNotFoundException;
import com.cakes.cakes.repository.CakeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CakeServiceTest {

    @Mock
    private CakeRepository cakeRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private DefaultCakeService cakeService;

    @Test
    public void getItemTest() {
        Cake cake = new Cake();
        cake.setId(1L);
        cake.setName("cake");
        cake.setStatus(StatusType.fresh);
        CompletableFuture<Cake> cakeCompletableFuture = CompletableFuture.completedFuture(cake);
        when(cakeRepository.getItem(1L)).thenReturn(cakeCompletableFuture);
        CompletableFuture<CakeDto> cakeServiceItem = cakeService.getItem(1L);
        CakeDto cakeDto;
        try {
            cakeDto = cakeServiceItem.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        assertEquals(cake.getId(), cakeDto.getId());
        assertEquals(cake.getName(), cakeDto.getName());
        assertEquals(cake.getStatus(), cakeDto.getStatus());
    }
}
