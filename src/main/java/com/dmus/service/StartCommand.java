package com.dmus.service;

import com.dmus.client.YandexClient;
import com.dmus.utils.InterviewLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.dmus.telegram.Bot;

import static com.dmus.utils.InterviewLevel.Junior;

@Slf4j
@Component
public class StartCommand extends Command {

    private static final String COMMAND_NAME = "/start";

    @Value("${gpt.prompts.interview}")
    private String questionPromptTemplate;

    public StartCommand(DBServiceQuestion questionService,
                        YandexClient yandexClient,
                        InterviewSessionsService interviewSessionsService,
                        Producer producer) {
        super(questionService, yandexClient, interviewSessionsService, producer);
    }

    public boolean isApplicable(Update update) {
        Message message = update.getMessage();
        return message.hasText() && COMMAND_NAME.equals(message.getText());
    }

    public String process(Update update, Bot bot) {
        log.trace("process > start processing {}", update.getMessage().getText());

        String userName = update.getMessage().getFrom().getUserName(); //TODO: currently UserName is used but not all accounts contain it
        InterviewLevel level = interviewSessionsService.getLevel(userName);
        if (level == null) { //if level was not set directly it is defaulted to Junior
            level = Junior;
            interviewSessionsService.setLevel(userName, level);
        }

        String baseQuestion = questionService.getRandomQuestion(level.getLevelNum());
        if (baseQuestion == null) {
            return "Для выбранного уровня нет вопросов!";
        }
        log.trace("process > configured level: '{}', selected baseQuestion: '{}'", level, baseQuestion);

        String prompt = String.format(questionPromptTemplate, level.name(), level.name(), baseQuestion);
        String question = yandexClient.promptModel(prompt);
        log.debug("process > received question from gpt: \n '{}'", question);

        interviewSessionsService.addQuestionAndStartTimer(userName, question, update.getMessage().getChatId(), bot);

        return question;
    }
}
