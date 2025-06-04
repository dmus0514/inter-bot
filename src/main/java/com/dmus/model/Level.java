package com.dmus.model;

import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Table("levels")
public record Level(
        @Id Long id,
        @Nonnull String name,
        String description,
        @MappedCollection(idColumn = "level_id") Set<Question> questions
) {}
