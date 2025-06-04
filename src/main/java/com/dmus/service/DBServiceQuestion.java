package com.dmus.service;

import com.dmus.model.Question;
import com.dmus.repository.QuestionsRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class DBServiceQuestion {

    private final QuestionsRepository questionsRepository;
    private final static Map<Long, Map<Long, List<String>>> questions = new HashMap<>(); //<level_id, <topic_id, [questions..]>>

    public DBServiceQuestion(QuestionsRepository questionsRepository) {
        this.questionsRepository = questionsRepository;
    }

    @PostConstruct
    public void init() {
        List<Question> questionList = questionsRepository.findAll();
        questionList.forEach(question -> {
            Map<Long, List<String>> levelMap = questions.computeIfAbsent(question.levelId(), k -> new HashMap<>());
            levelMap.computeIfAbsent(question.topicId(), k -> new ArrayList<>()).add(question.question());
        });
        log.trace("init > questions: \n{}", questions);
    }

    public String getRandomQuestion(Long levelId) {
        var topicMap = questions.get(levelId);
        if (topicMap == null) {
            log.error("getRandomQuestion > there is no questions for specified level");
            return null;
        }
        var questionList = topicMap.get((long)(Math.random() * topicMap.size() + 1));

        return questionList.get((int)(Math.random() * questionList.size()));
    }

    public List<Question> findAllQuestions() {
        return questionsRepository.findAllSorted();
    }

    public Optional<Question> findQuestionById(long questionId) {
        return questionsRepository.findById(questionId);
    }

    public Question saveQuestion(Question question) {
        return questionsRepository.save(question);
    }

    public void deleteQuestion(Long questionId) {
        questionsRepository.deleteById(questionId);
    }

    public void updateQuestion(Long questionId, Long levelId, Long topicId, String questionText) {
        questionsRepository.updateQuestion(questionId, levelId, topicId, questionText);
    }
}
