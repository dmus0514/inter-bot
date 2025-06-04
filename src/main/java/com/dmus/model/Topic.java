package com.dmus.model;

import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Table("topics")
public record Topic(
        @Id Long id,
        @Nonnull String topic,
        @MappedCollection(idColumn = "topic_id") Set<Question> questions
) {}
