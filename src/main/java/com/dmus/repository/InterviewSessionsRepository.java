package com.dmus.repository;

import com.dmus.utils.InterviewLevel;
import org.springframework.stereotype.Repository;
import com.dmus.dto.Question;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Repository
public class InterviewSessionsRepository {

    private final Map<String, UserSession> userQuestions = new ConcurrentHashMap<>();

    public void setLevel(String userName, InterviewLevel level) {
        userQuestions.put(userName, new UserSession(level));
    }

    public InterviewLevel getLevel(String userName) {
        UserSession userSession = userQuestions.get(userName);

        if (userSession == null) {
            return null;
        } else {
            return userQuestions.get(userName).interviewLevel;
        }
    }

    public void addQuestion(String userName, String question) {
        Question dto = new Question();
        dto.setQuestion(question);

        userQuestions.computeIfAbsent(userName, k -> new UserSession()).addQuestion(dto);
    }

    public Question addAnswer(String userName, String answer) {
        if (userQuestions.containsKey(userName)) {
            Question question = userQuestions.get(userName).getLastQuestion();
            if (question != null) {
                question.setAnswer(answer);
                return question;
            } else {
                throw new IllegalStateException("There is no question being answered for user " + userName);
            }
        } else {
            throw new IllegalStateException("There is no interview session started for user " + userName);
        }
    }

    public Deque<Question> finishInterview(String userName) {
        return userQuestions.remove(userName).getQuestions();
    }

    public int getAskedQuestionsNumberPerUser(String userName) {
        return userQuestions.get(userName).getQuestions().size();
    }

    public ScheduledFuture<?> getTimerJob(String userName) {
        return userQuestions.get(userName).timerJob;
    }

    public void setTimerJob(String userName, ScheduledFuture<?> timerJob) {
        userQuestions.get(userName).timerJob = timerJob;
    }

    private static class UserSession {
        private final InterviewLevel interviewLevel;
        private final Deque<Question> questions;
        private ScheduledFuture<?> timerJob;

        public UserSession() {
            this(InterviewLevel.Junior, new LinkedList<>());
        }

        public UserSession(InterviewLevel level) {
            this(level, new LinkedList<>());
        }

        public UserSession(InterviewLevel level, Deque<Question> questions) {
            this.interviewLevel = level;
            this.questions = questions;
        }

        private void addQuestion(Question question) {
            questions.add(question);
        }

        private Question getLastQuestion() {
            return questions.peekLast();
        }

        private Deque<Question> getQuestions() {
            return questions;
        }
    }

}
