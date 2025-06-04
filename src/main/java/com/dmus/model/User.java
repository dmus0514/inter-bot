package com.dmus.model;

import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Table("users")
public record User(
    @Id Long id,
    @Nonnull String tgFirstname,
    String tgLastname,
    String tgUsername,
    String mobilePhone,
    @MappedCollection(idColumn = "user_id") Set<Interview> interviews
) {}
