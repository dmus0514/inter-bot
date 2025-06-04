package com.dmus.service;

import com.dmus.dto.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Producer {
    private static final String TOPIC = "userss";
    //protected final IUserRepository repo;

    @Autowired
    private KafkaTemplate<String, Question> kafkaTemplate;

    /*public Producer(IUserRepository repo) {
        this.repo = repo;
    }*/

    public void sendMessage(Question message) {
        log.info("sendMessage > send message to Kafka [message={}]", message);
        kafkaTemplate.send(TOPIC, message);
    }
}
