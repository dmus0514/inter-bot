package com.dmus;

import com.dmus.client.YandexClient;
import com.dmus.service.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;

@SpringBootTest
class CommandTest {

    @Autowired
    DBServiceQuestion questionService;

    @Autowired
    InterviewSessionsService interviewSessionsService;

    @Autowired
    YandexClient yandexClient;

    @Autowired
    Producer producer;

    /*@MockitoBean
    Bot bot;*/

    /*@BeforeAll
    void setUp() {

    }*/

    @Disabled
    @Test
    public void runCommandTest() {
        Command cmd = new StartCommand(questionService, yandexClient, interviewSessionsService, producer);
        Update update = new Update();
        Mockito.when(update.getMessage().getFrom().getUserName()).thenReturn("User1");
        String gptAnswer = cmd.process(update, null);
        System.out.println(gptAnswer);
    }

}