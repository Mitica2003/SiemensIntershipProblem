package com.siemens.internship.controller;

import com.siemens.internship.model.Item;
import com.siemens.internship.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing CRUD endpoints for Item entities.
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Retrieves all items.
     *
     * @return 200 OK with list of items (empty list if none)
     */
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.findAll();
        return ResponseEntity
                .ok(items); // 200 OK
    }

    /**
     * Creates a new item.
     *
     * @param item validated item payload
     * @return 201 Created with saved item
     */
    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) {
        Item savedItem = itemService.save(item);
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 Created
                .body(savedItem);
    }

    /**
     * Retrieves an item by ID.
     *
     * @param id identifier of the item
     * @return 200 OK with item or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(ResponseEntity::ok) // 200 OK
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND) // 404 Not Found
                        .build());
    }

    /**
     * Updates an existing item.
     *
     * @param id   identifier of the item to update
     * @param item validated payload with updated fields
     * @return 200 OK with updated item, 400 Bad Request on validation errors,
     *         or 404 Not Found if the item does not exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody Item item) {

        return itemService.findById(id)
                .map(existing -> {
                    item.setId(id);
                    Item updated = itemService.save(item);
                    return ResponseEntity
                            .ok(updated); // 200 OK
                })
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND) // 404 Not Found
                        .build());
    }

    /**
     * Deletes an item by ID.
     *
     * @param id identifier of the item to delete
     * @return 204 No Content if deleted or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        boolean deleted = itemService.deleteById(id);
        if (deleted) {
            return ResponseEntity
                    .noContent() // 204 No Content
                    .build();
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND) // 404 Not Found
                    .build();
        }
    }

    /**
     * Processes all items asynchronously.
     *
     * @return 200 OK with list of processed items, or 500 Internal Server Error on failure
     */
    @GetMapping("/process")
    public ResponseEntity<List<Item>> processItems() {
        List<Item> processedItems = itemService.processItemsAsync();
        return ResponseEntity
                .ok(processedItems); // 200 OK
    }
}
