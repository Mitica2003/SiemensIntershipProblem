package com.siemens.internship.repository;

import com.siemens.internship.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on Item entities.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * Retrieves all item IDs, used for batch processing.
     *
     * @return list of item IDs
     */
    @Query("SELECT id FROM Item")
    List<Long> findAllIds();
}
