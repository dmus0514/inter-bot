package com.dmus.model;

import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Set;

@Table("interviews")
public record Interview(
        @Id Long id,
        Long userId,
        @Nonnull LocalDateTime date,
        @Nonnull Long levelId,
        String feedback,
        Integer resultGrade,
        @MappedCollection(idColumn = "interview_id") Set<AskedQuestion> askedQuestions
) {}
