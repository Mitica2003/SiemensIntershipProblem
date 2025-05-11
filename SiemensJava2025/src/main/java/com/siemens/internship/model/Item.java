package com.siemens.internship.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Represents an item with details and processing status.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    /** Unique identifier for the item. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** Human-readable name of the item. */
    @NotBlank(message = "Name mustn't be blank")
    private String name;

    /** Detailed description of the item. */
    @NotBlank(message = "Description mustn't be blank")
    private String description;

    /** Processing status, e.g., "NEW" or "PROCESSED". */
    private String status;

    /** Notification email for the item; must be valid. */
    @NotBlank(message = "Email mustn't be blank")
    @Email(message = "Email should be valid")
    private String email;
}