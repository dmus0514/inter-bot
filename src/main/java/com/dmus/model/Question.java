package com.dmus.model;

import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("questions")
public record Question(
        @Id Long id,
        @Nonnull Long levelId,
        @Nonnull Long topicId,
        @Nonnull String question
) {}
