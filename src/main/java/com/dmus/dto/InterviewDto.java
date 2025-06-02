package com.dmus.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record InterviewDto(Long id, String user, LocalDateTime date, String level, String feedback, Integer resultGrade, Set<Question> askedQuestions) {}
