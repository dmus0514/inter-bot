package com.dmus.model;

import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("asked_questions")
public record AskedQuestion(
        @Id Long id,
        Long interviewId,
        @Nonnull Long usedQuestion,
        @Nonnull String questionText,
        @Nonnull String answerText
        ) {}
