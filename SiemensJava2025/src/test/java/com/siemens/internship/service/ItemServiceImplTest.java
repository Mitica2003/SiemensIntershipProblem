package com.siemens.internship.service;

import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import com.siemens.internship.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setup() {
        item1 = new Item(1L, "Item1", "Desc1", "NEW", "a@example.com");
        item2 = new Item(2L, "Item2", "Desc2", "NEW", "b@example.com");
    }

    @Test
    void findAll_shouldReturnAllItems() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));
        List<Item> result = itemService.findAll();
        assertEquals(2, result.size());
        verify(itemRepository).findAll();
    }

    @Test
    void findById_existingId_shouldReturnItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        Optional<Item> result = itemService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(item1, result.get());
    }

    @Test
    void findById_nonExistingId_shouldReturnEmpty() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Item> result = itemService.findById(99L);
        assertFalse(result.isPresent());
    }

    @Test
    void save_shouldPersistAndReturnItem() {
        when(itemRepository.save(any(Item.class))).thenReturn(item1);
        Item saved = itemService.save(item1);
        assertEquals(item1, saved);
        verify(itemRepository).save(item1);
    }

    @Test
    void deleteById_existingId_shouldReturnTrue() {
        doNothing().when(itemRepository).deleteById(1L);
        assertTrue(itemService.deleteById(1L));
    }

    @Test
    void deleteById_nonExistingId_shouldReturnFalse() {
        doThrow(new RuntimeException()).when(itemRepository).deleteById(99L);
        assertFalse(itemService.deleteById(99L));
    }

    @Test
    void processItemsAsync_allItemsProcessed() throws ExecutionException, InterruptedException {
        when(itemRepository.findAllIds()).thenReturn(Arrays.asList(1L, 2L));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        List<Item> processed = itemService.processItemsAsync();
        assertEquals(2, processed.size());
        assertEquals("PROCESSED", processed.get(0).getStatus());
        assertEquals("PROCESSED", processed.get(1).getStatus());
        verify(itemRepository, times(2)).save(any(Item.class));
    }
}
