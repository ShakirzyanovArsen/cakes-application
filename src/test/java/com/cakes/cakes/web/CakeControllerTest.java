package com.cakes.cakes.web;

import com.cakes.cakes.domain.Cake;
import com.cakes.cakes.domain.CakeDto;
import com.cakes.cakes.domain.CakeFilter;
import com.cakes.cakes.domain.StatusType;
import com.cakes.cakes.repository.CakeRepository;
import com.cakes.cakes.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CakeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CakeRepository cakeRepository;


    @Test
    public void getViewEmptyTest() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/api/cakes"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.total", is(0)));
    }

    @Test
    public void getViewFilterTest() throws Exception {
        Cake cake1 = ObjectUtils.createCake(null, "Cake 1", StatusType.fresh);
        Cake cake2 = ObjectUtils.createCake(null, "Cake 2", StatusType.stale);
        Cake cake3 = ObjectUtils.createCake(null, "Cake 3", StatusType.fresh);
        cakeRepository.addItem(cake1).join();
        cakeRepository.addItem(cake2).join();
        cakeRepository.addItem(cake3).join();
        CakeFilter cakeFilter = ObjectUtils.createCakeFilter(10, 1, new StatusType[]{StatusType.fresh}, "Cake 1");
        ObjectMapper objectMapper = new ObjectMapper();
        String filterJson = objectMapper.writeValueAsString(cakeFilter);

        MvcResult mvcResult = mvc.perform(get("/api/cakes?cakeFilter={json}", filterJson))
                .andExpect(request().asyncStarted())
                .andReturn();
        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name").value("Cake 1"))
                .andExpect(jsonPath("$.items[0].status").value("fresh"))
                .andExpect(jsonPath("$.total", is(1)));
    }

    @Test
    public void getItemTest() throws Exception {
        Cake cake = ObjectUtils.createCake(null, "Cake 1", StatusType.fresh);
        cakeRepository.addItem(cake).join();

        MvcResult mvcResult = mvc.perform(get("/api/cakes/" + cake.getId()))
                .andExpect(request().asyncStarted())
                .andReturn();
        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value(cake.getId()))
                .andExpect(jsonPath("$.name").value(cake.getName()))
                .andExpect(jsonPath("$.status").value(cake.getStatus().value()));
    }

    @Test
    public void getItemNotFoundTest() throws Exception {
        Cake cake = ObjectUtils.createCake(null, "Cake 1", StatusType.fresh);
        cakeRepository.addItem(cake).join();

        MvcResult mvcResult = mvc.perform(get("/api/cakes/" + (cake.getId() + 1)))
                .andExpect(request().asyncStarted())
                .andReturn();
        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void saveNewCakeTest() throws Exception {
        CakeDto cakeDto = ObjectUtils.createCakeDto(null, "Cake", StatusType.fresh);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(cakeDto);

        MvcResult mvcResult = mvc.perform(post("/api/cakes")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();
        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk());

        Cake cakeInDb = cakeRepository.getItem(1L).join();
        assertEquals(cakeInDb.getName(), cakeDto.getName());
        assertEquals(cakeInDb.getStatus(), cakeDto.getStatus());
    }

    @Test
    public void saveUpdateCakeTest() throws Exception {
        Cake cakeOld = ObjectUtils.createCake(null, "CakeOld", StatusType.stale);
        cakeRepository.addItem(cakeOld).join();
        CakeDto cakeDto = ObjectUtils.createCakeDto(cakeOld.getId(), "Cake", StatusType.fresh);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(cakeDto);

        MvcResult mvcResult = mvc.perform(post("/api/cakes")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk());

        Cake cakeInDb = cakeRepository.getItem(cakeOld.getId()).join();
        assertEquals(cakeInDb.getName(), cakeDto.getName());
        assertEquals(cakeInDb.getStatus(), cakeDto.getStatus());
    }

    @Test
    public void saveUpdateCakeNotFoundTest() throws Exception {
        Cake cakeOld = ObjectUtils.createCake(null, "CakeOld", StatusType.stale);
        cakeRepository.addItem(cakeOld).join();
        CakeDto cakeDto = ObjectUtils.createCakeDto(cakeOld.getId() + 1, "Cake", StatusType.fresh);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(cakeDto);

        MvcResult mvcResult = mvc.perform(post("/api/cakes")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void saveUpdateCakeNotStaleableTest() throws Exception {
        Cake cakeOld = ObjectUtils.createCake(null, "CakeOld", StatusType.fresh);
        cakeRepository.addItem(cakeOld).join();
        CakeDto cakeDto = ObjectUtils.createCakeDto(cakeOld.getId(), "Cake", StatusType.stale);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(cakeDto);

        MvcResult mvcResult = mvc.perform(post("/api/cakes")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void removeCakeTest() throws Exception {
        Cake cake = ObjectUtils.createCake(null, "CakeOld", StatusType.fresh);
        cakeRepository.addItem(cake).join();
        Long cakeId = cake.getId();

        MvcResult mvcResult = mvc.perform(delete("/api/cakes/" + cakeId))
                .andExpect(request().asyncStarted())
                .andReturn();
        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk());

        Cake cakeInDb = cakeRepository.getItem(cakeId).join();
        assertNull(cakeInDb);
    }

    @Test
    public void removeCakeNotFoundTest() throws Exception {
        Cake cake = ObjectUtils.createCake(null, "CakeOld", StatusType.fresh);
        cakeRepository.addItem(cake).join();
        Long cakeId = cake.getId() + 1;

        MvcResult mvcResult = mvc.perform(delete("/api/cakes/" + cakeId))
                .andExpect(request().asyncStarted())
                .andReturn();
        mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.description").exists());

        Cake cakeInDb = cakeRepository.getItem(cake.getId()).join();
        assertNotNull(cakeInDb);
    }
}
