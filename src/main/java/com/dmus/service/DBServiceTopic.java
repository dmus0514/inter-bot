package com.dmus.service;

import com.dmus.model.Topic;
import com.dmus.repository.TopicsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DBServiceTopic {
    private final TopicsRepository topicsRepository;

    public DBServiceTopic(TopicsRepository topicsRepository) {
        this.topicsRepository = topicsRepository;
    }

    public List<Topic> findAll() {
        return topicsRepository.findAll();
    }

    public Topic saveTopic(Topic topic) {
        return topicsRepository.save(topic);
    }

    public Optional<Topic> findTopicById(Long id) {
        return topicsRepository.findById(id);
    }

    public void deleteTopicById(Long id) {
        topicsRepository.deleteById(id);
    }
}
