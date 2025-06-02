package com.dmus.repository;

import com.dmus.model.Level;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface LevelsRepository extends ListCrudRepository<Level, Long> {
    Optional<Level> findLevelById(Long id);
}
