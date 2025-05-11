package com.siemens.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.internship.model.Item;
import com.siemens.internship.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private Item item1;

    @BeforeEach
    void setup() {
        item1 = new Item(1L, "Item1", "Desc1", "NEW", "a@example.com");
    }

    @Test
    void createItem_validInput_shouldReturnCreated() throws Exception {
        when(itemService.save(any(Item.class))).thenReturn(item1);
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateItem_invalidEmail_shouldReturnBadRequest() throws Exception {
        Item badEmailItem = new Item(null, "ValidName", "ValidDesc", "NEW", "not-an-email");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badEmailItem)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_invalidEmail_shouldReturnBadRequest() throws Exception {
        Item bad = new Item(null, "Name", "Desc", "NEW", "invalid-email");
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemById_existingId_shouldReturnOk() throws Exception {
        when(itemService.findById(1L)).thenReturn(Optional.of(item1));
        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item1"));
    }

    @Test
    void getItemById_nonExistingId_shouldReturnNotFound() throws Exception {
        when(itemService.findById(2L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/items/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteItem_existingId_shouldReturnNoContent() throws Exception {
        when(itemService.deleteById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/items/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteItem_nonExistingId_shouldReturnNotFound() throws Exception {
        when(itemService.deleteById(2L)).thenReturn(false);
        mockMvc.perform(delete("/api/items/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void processItems_shouldReturnProcessedList() throws Exception {
        when(itemService.processItemsAsync()).thenReturn(Arrays.asList(item1));
        mockMvc.perform(get("/api/items/process"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("NEW"));
    }
}
