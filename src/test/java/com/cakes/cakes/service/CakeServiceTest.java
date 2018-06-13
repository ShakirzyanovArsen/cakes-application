package com.cakes.cakes.service;

import com.cakes.cakes.domain.*;
import com.cakes.cakes.exception.CakeNotStaleableException;
import com.cakes.cakes.exception.EntityNotFoundException;
import com.cakes.cakes.repository.CakeRepository;
import com.cakes.cakes.utils.ObjectUtils;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Captor
    private ArgumentCaptor<Cake> cakeCaptor;

    @Test
    public void getItemTest() {
        Cake cake = ObjectUtils.createCake(1L, "Cake", StatusType.fresh);
        CompletableFuture<Cake> cakeCompletableFuture = CompletableFuture.completedFuture(cake);
        when(cakeRepository.getItem(1L)).thenReturn(cakeCompletableFuture);
        CompletableFuture<CakeDto> cakeServiceItem = cakeService.getItem(1L);
        CakeDto cakeDto = cakeServiceItem.join();

        assertEquals(cake.getId(), cakeDto.getId());
        assertEquals(cake.getName(), cakeDto.getName());
        assertEquals(cake.getStatus(), cakeDto.getStatus());
    }

    @Test
    public void getItemNotFoundTest() {
        CompletableFuture<Cake> nullCakeFuture = CompletableFuture.completedFuture(null);
        when(cakeRepository.getItem(1L)).thenReturn(nullCakeFuture);
        CompletableFuture<CakeDto> cakeServiceItem = cakeService.getItem(1L);
        expectedException.expectCause(IsInstanceOf.instanceOf(EntityNotFoundException.class));
        cakeServiceItem.join();
    }

    @Test
    public void getViewTest() {
        CakeFilter filter = ObjectUtils.createCakeFilter(1, 1, new StatusType[]{StatusType.stale}, "Cake1");
        Cake cake = ObjectUtils.createCake(1L, "Cake", StatusType.stale);
        List<Cake> cakes = Collections.singletonList(cake);
        CompletableFuture<List<Cake>> cakesFuture = CompletableFuture.completedFuture(cakes);
        when(cakeRepository.getRange(filter)).thenReturn(cakesFuture);
        when(cakeRepository.getTotal(filter)).thenReturn(CompletableFuture.completedFuture(1L));
        CompletableFuture<CakeView> viewFuture = cakeService.getView(filter);
        CakeView view = viewFuture.join();
        assertTrue(view.getTotal().equals(1L));
        List<CakeDto> cakeDtoList = view.getItems();

        assertEquals(cakes.size(), cakeDtoList.size());
        assertEquals(1, cakeDtoList.size());
        assertEquals(cake.getId(), cakeDtoList.get(0).getId());
        assertEquals(cake.getName(), cakeDtoList.get(0).getName());
        assertEquals(cake.getStatus(), cakeDtoList.get(0).getStatus());
    }

    @Test
    public void saveNewItemTest() {
        Cake cake = ObjectUtils.createCake(null,"Cake", StatusType.fresh);
        cakeService.saveItem(ObjectUtils.createDtoByCake(cake));
        verify(cakeRepository, times(1)).addItem(cakeCaptor.capture());
        Cake capturedCake = cakeCaptor.getValue();
        assertEquals(cake.getName(), capturedCake.getName());
        assertEquals(cake.getStatus(), capturedCake.getStatus());
    }

    @Test
    public void saveUpdateItemTest() {
        Cake cake = ObjectUtils.createCake(1L,"Cake", StatusType.fresh);
        Cake cakeOld = ObjectUtils.createCake(1L,"Cake old", StatusType.fresh);
        when(cakeRepository.getItem(1L)).thenReturn(CompletableFuture.completedFuture(cakeOld));
        cakeService.saveItem(ObjectUtils.createDtoByCake(cake));

        verify(cakeRepository, times(1)).updateItem(cakeCaptor.capture());
        Cake capturedCake = cakeCaptor.getValue();
        assertEquals(cake.getId(), capturedCake.getId());
        assertEquals(cake.getName(), capturedCake.getName());
        assertEquals(cake.getStatus(), capturedCake.getStatus());
    }

    @Test(expected = EntityNotFoundException.class)
    public void saveUpdateItemNotFoundTest() {
        Cake cake = ObjectUtils.createCake(1L,"Cake", StatusType.fresh);
        when(cakeRepository.getItem(1L)).thenReturn(CompletableFuture.completedFuture(null));
        cakeService.saveItem(ObjectUtils.createDtoByCake(cake));

        verify(cakeRepository, times(0)).updateItem(cakeCaptor.capture());
    }

    @Test(expected = CakeNotStaleableException.class)
    public void saveUpdateItemCakeNotStaleable() {
        Cake cake = ObjectUtils.createCake(1L,"Cake", StatusType.stale);
        Cake cakeOld = ObjectUtils.createCake(1L,"Cake old", StatusType.fresh);
        when(cakeRepository.getItem(1L)).thenReturn(CompletableFuture.completedFuture(cakeOld));
        cakeService.saveItem(ObjectUtils.createDtoByCake(cake));
    }

    @Test
    public void removeItemTest() {
        Cake cake = ObjectUtils.createCake(1L,"Cake", StatusType.fresh);
        when(cakeRepository.getItem(1L)).thenReturn(CompletableFuture.completedFuture(cake));
        cakeService.removeItem(1L);

        verify(cakeRepository).removeItem(cakeCaptor.capture());
        Cake capturedCake = cakeCaptor.getValue();
        assertEquals(cake.getId(), capturedCake.getId());
        assertEquals(cake.getName(), capturedCake.getName());
        assertEquals(cake.getStatus(), capturedCake.getStatus());
    }

    @Test
    public void removeItemNotFoundTest() {
        when(cakeRepository.getItem(1L)).thenReturn(CompletableFuture.completedFuture(null));
        expectedException.expectCause(IsInstanceOf.instanceOf(EntityNotFoundException.class));
        cakeService.removeItem(1L).join();
    }

    @Test
    public void getTotalTest() {
        CakeFilter cakeFilter = ObjectUtils.createCakeFilter(1, 1, new StatusType[]{StatusType.stale}, "Cake1");
        when(cakeRepository.getTotal(cakeFilter)).thenReturn(CompletableFuture.completedFuture(100L));
        Long total = cakeService.getTotal(cakeFilter).join();
        assertTrue(total.equals(100L));
    }
}
