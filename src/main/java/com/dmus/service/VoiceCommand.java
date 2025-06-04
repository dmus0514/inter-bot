package com.dmus.service;

import com.dmus.client.YandexClient;
import com.dmus.model.AskedQuestion;
import com.dmus.model.Interview;
import com.dmus.model.User;
import com.dmus.utils.InterviewLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.dmus.dto.Question;
import com.dmus.telegram.Bot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class VoiceCommand extends Command {

    @Value("${interview.max-questions}")
    private int maxQuestions;

    @Value("${gpt.prompts.question}")
    private String questionPromptTemplate;

    @Value("${gpt.prompts.feedback}")
    private String feedbackPromptTemplate;

    DBServiceUser dbServiceUser;

    public VoiceCommand(YandexClient yandexClient,
                        InterviewSessionsService interviewSessionsService,
                        DBServiceQuestion questionService,
                        Producer producer,
                        DBServiceUser dbServiceUser) {
        super(questionService, yandexClient, interviewSessionsService, producer);
        this.dbServiceUser = dbServiceUser;
    }

    @Override
    public boolean isApplicable(Update update) {
        return update.getMessage().hasVoice();
    }

    @Override
    public String process(Update update, Bot bot) {
        log.trace("process > start processing voice message");
        String answer = transcribeVoiceAnswer(update, bot);
        log.debug("process > received transcribed answer from gpt: \n '{}'", answer);

        String userName = update.getMessage().getFrom().getUserName();
        try {
            var question = interviewSessionsService.addAnswer(userName, answer);
            interviewSessionsService.stopTimer(userName);

            producer.sendMessage(question);

            InterviewLevel level = interviewSessionsService.getLevel(userName);
            if (interviewSessionsService.getAskedQuestionsNumberPerUser(userName) == maxQuestions) {
                return provideFeedback(update, level);
            } else {
                return askNextQuestion(update, level, bot);
            }
        } catch (Exception e) {
            log.error("process > Exception in processing voice message", e);
            return "Что-то пошло не так! Возможно нет активного собеседования! Пожалуйста начните заново!";
        }
    }

    private String transcribeVoiceAnswer(Update update, Bot bot) {
        Voice voice = update.getMessage().getVoice();
        String fileId = voice.getFileId();
        java.io.File audio;
        try {
            GetFile getFileRequest = new GetFile();
            getFileRequest.setFileId(fileId);
            File file = bot.execute(getFileRequest);

            audio = bot.downloadFile(file.getFilePath());
        } catch (TelegramApiException e) {
            throw new IllegalStateException("There's an error when processing Telegram audio", e);
        }
        return yandexClient.transcribe(renameToOgg(audio));
    }

    private java.io.File renameToOgg(java.io.File tmpFile) {
        String fileName = tmpFile.getName();
        String newFileName = fileName.substring(0, fileName.length() - 4) + ".ogg";
        Path sourcePath = tmpFile.toPath();
        Path targetPath = sourcePath.resolveSibling(newFileName);
        try {
            Files.move(sourcePath, targetPath);
        } catch (IOException e) {
            throw new IllegalStateException("There was an error when renaming .tmp audio file to .ogg", e);
        }
        return targetPath.toFile();
    }

    private String askNextQuestion(Update update, InterviewLevel level, Bot bot) {
        log.trace("askNextQuestion > start processing");

        String baseQuestion = questionService.getRandomQuestion(level.getLevelNum());
        if (baseQuestion == null) {
            return "Для выбранного уровня нет вопросов!";
        }
        log.trace("askNextQuestion > configured level: '{}', selected baseQuestion: '{}'", level, baseQuestion);

        String prompt = String.format(questionPromptTemplate, level.name(), baseQuestion);
        String question = yandexClient.promptModel(prompt);
        log.debug("askNextQuestion > received question from chatgpt: \n '{}'", question);

        interviewSessionsService.addQuestionAndStartTimer(update.getMessage().getFrom().getUserName(), question,
                update.getMessage().getChatId(), bot);

        return question;
    }

    private String provideFeedback(Update update, InterviewLevel level) {
        log.trace("provideFeedback > start processing");

        StringBuilder feedbackPrompt = new StringBuilder();
        feedbackPrompt.append(String.format(feedbackPromptTemplate, level.name()));

        org.telegram.telegrambots.meta.api.objects.User tgUser = update.getMessage().getFrom();
        String userName = tgUser.getUserName();

        Deque<Question> questions = interviewSessionsService.finishInterview(userName);
        questions.forEach(question -> feedbackPrompt.append("Исходный вопрос: ")
                .append(question.getQuestion()).append("\n")
                .append("Ответ кандидата: ")
                .append(question.getAnswer()).append("\n"));

        Set<AskedQuestion> askedQuestions = new HashSet<>();
        questions.forEach(question -> askedQuestions.add(
                new AskedQuestion(null, null, (long)(Math.random() * 10 + 1), question.getQuestion(), question.getAnswer())
        ));
        log.debug("provideFeedback > askedQuestions: {}", askedQuestions);
        String feedback = yandexClient.promptModel(feedbackPrompt.toString());
        Integer resultGrade = 5; //TODO: add request to prompt about user grade
        /*long epochFromDate = LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .getEpochSecond();*/

        Interview interview = new Interview(null, null, LocalDateTime.now(), 1L, feedback, resultGrade, askedQuestions);

        var userOptional = dbServiceUser.getUserByName(userName);
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            user.interviews().add(interview);
            dbServiceUser.saveUser(user);
            //dbServiceUser.saveUser(new User(user.id(), user.getFirstName(), user.getLastName(), user.getUserName(), null, interviews));
        } else {
            dbServiceUser.saveUser(new User(null, tgUser.getFirstName(), tgUser.getLastName(), tgUser.getUserName(),
                    null, Set.of(interview)));
        }

        return yandexClient.promptModel(feedbackPrompt.toString());
    }
}
