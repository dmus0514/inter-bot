package com.dmus.service;

import com.dmus.model.Level;
import com.dmus.repository.LevelsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DBServiceLevel {
    private final LevelsRepository levelsRepository;

    public DBServiceLevel(LevelsRepository repo) {
        this.levelsRepository = repo;
    }

    public List<Level> findAll() {
        return levelsRepository.findAll();
    }

    public Level saveLevel(Level level) {
        return levelsRepository.save(level);
    }

    public Optional<Level> findLevelById(Long id) {
        return levelsRepository.findLevelById(id);
    }

    public void deleteLevelById(Long id) {
        levelsRepository.deleteById(id);
    }
}
