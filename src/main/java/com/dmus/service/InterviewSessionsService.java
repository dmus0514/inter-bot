package com.dmus.service;

import com.dmus.dto.Question;
import com.dmus.repository.InterviewSessionsRepository;
import com.dmus.telegram.Bot;
import com.dmus.utils.InterviewLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Deque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class InterviewSessionsService {

    public static final Integer SESSION_TIMEOUT = 120;

    private final InterviewSessionsRepository interviewSessionsRepository;
    private final ScheduledExecutorService timerJobExecutor;

    public InterviewSessionsService(InterviewSessionsRepository interviewSessionsRepository) {
        this.interviewSessionsRepository = interviewSessionsRepository;
        timerJobExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    public void setLevel(String userName, InterviewLevel level) {
        interviewSessionsRepository.setLevel(userName, level);
    }

    public InterviewLevel getLevel(String userName) {
        return interviewSessionsRepository.getLevel(userName);
    }

    public void addQuestionAndStartTimer(String userName, String question, Long chatId, Bot bot) {
        interviewSessionsRepository.addQuestion(userName, question);
        ScheduledFuture<?> timerJob = timerJobExecutor.schedule(
                () -> {
                    try {
                        bot.execute(new SendMessage(chatId.toString(), "Время на ответ истекло, собеседование удаляется, пожалуйста начните заново!"));
                        interviewSessionsRepository.finishInterview(userName);
                    } catch (TelegramApiException e) {
                        log.error("checkActivityJob > Exception occurs while sending message to Telegram bot: {}", e.getMessage());
                    }
                }, SESSION_TIMEOUT, TimeUnit.SECONDS
        );
        interviewSessionsRepository.setTimerJob(userName, timerJob);
    }

    public Question addAnswer(String userName, String answer) {
        return interviewSessionsRepository.addAnswer(userName, answer);
    }

    public Deque<Question> finishInterview(String userName) {
        return interviewSessionsRepository.finishInterview(userName);
    }

    public int getAskedQuestionsNumberPerUser(String userName) {
        return interviewSessionsRepository.getAskedQuestionsNumberPerUser(userName);
    }

    public ScheduledFuture<?> getTimerJob(String userName) {
        return interviewSessionsRepository.getTimerJob(userName);
    }

    public void setTimerJob(String userName, ScheduledFuture<?> timerJob) {
        interviewSessionsRepository.setTimerJob(userName, timerJob);
    }

    public void stopTimer(String userName) {
        interviewSessionsRepository.getTimerJob(userName).cancel(true);
    }

}
