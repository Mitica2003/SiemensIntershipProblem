package com.siemens.internship.service;

import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Service layer handling business logic for Item operations.
 */
@Service
public class ItemServiceImpl implements ItemService{
    private final ItemRepository itemRepository;

    /**
     * Thread pool for asynchronous processing tasks.
     */
    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Retrieves all items from the repository.
     *
     * @return list of items
     */
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    /**
     * Finds an item by its ID.
     *
     * @param id identifier of the item
     * @return Optional containing the item if found
     */
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    /**
     * Persists the given item.
     *
     * @param item entity to save
     * @return saved item with generated ID
     */
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    /**
     * Deletes an item by ID, returning whether it existed.
     *
     * @param id identifier of the item to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteById(Long id) {
        try {
            itemRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    /**
     * Processes all items asynchronously by marking them as PROCESSED.
     * Uses a fixed thread pool and waits for all tasks to complete before returning.
     *
     * @return list of processed items
     */
    @Transactional
    public List<Item> processItemsAsync() {
        // 1. Fetch all IDs
        List<Long> itemIds = itemRepository.findAllIds();

        // 2. Parallel processing: update status & save
        return itemIds.parallelStream()
                .map(id -> itemRepository.findById(id)
                        .map(item -> {
                            // mark as processed
                            item.setStatus("PROCESSED");
                            return itemRepository.save(item);
                        })
                        .orElseThrow(() -> new RuntimeException("Item not found: " + id))
                )
                .collect(Collectors.toList());
    }

}
