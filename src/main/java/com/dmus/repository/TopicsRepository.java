package com.dmus.repository;

import com.dmus.model.Topic;
import org.springframework.data.repository.ListCrudRepository;

public interface TopicsRepository extends ListCrudRepository<Topic, Long> {
}
