package com.dmus.controllers;

import com.dmus.dto.LevelDto;
import com.dmus.dto.QuestionDto;
import com.dmus.dto.TopicDto;
import com.dmus.model.Level;
import com.dmus.model.Question;
import com.dmus.model.Topic;
import com.dmus.service.DBServiceLevel;
import com.dmus.service.DBServiceQuestion;
import com.dmus.service.DBServiceTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class QuestionsController {

    private final DBServiceQuestion dbServiceQuestion;
    private final DBServiceTopic dbServiceTopic;
    private final DBServiceLevel dbServiceLevel;

    public QuestionsController(DBServiceQuestion dbServiceQuestion, DBServiceLevel dbServiceLevel,
                               DBServiceTopic dbServiceTopic) {
        this.dbServiceQuestion = dbServiceQuestion;
        this.dbServiceLevel = dbServiceLevel;
        this.dbServiceTopic = dbServiceTopic;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "main";
    }

    @PostMapping("/topics")
    public RedirectView createTopic(TopicDto topicDto) {
        log.debug("topic to be created: " + topicDto);
        Topic topic = new Topic(null, topicDto.topic(), null);
        dbServiceTopic.saveTopic(topic);
        return new RedirectView("/questions", true);
    }

    @PostMapping("/levels")
    public RedirectView createLevel(LevelDto levelDto) {
        log.debug("level to be created: " + levelDto);
        Level level = new Level(levelDto.id(), levelDto.name(), levelDto.description(), null);
        dbServiceLevel.saveLevel(level);
        return new RedirectView("/questions", true);
    }

    @GetMapping("/questions")
    public String questionsListView(Model model) {
        List<Question> questions = dbServiceQuestion.findAllQuestions();
        List<Level> levels = dbServiceLevel.findAll();
        List<Topic> topics = dbServiceTopic.findAll();
        model.addAttribute("questions", questions);
        model.addAttribute("levels", levels);
        model.addAttribute("topics", topics);
        return "questionList";
    }

    @PostMapping("/questions")
    public RedirectView createQuestion(QuestionDto questionDto) {
        log.info("Question created: " + questionDto);
        Question question = new Question(questionDto.id(), questionDto.levelId(), questionDto.topicId(), questionDto.question());
        dbServiceQuestion.saveQuestion(question);
        return new RedirectView("/questions", true);
    }

    @GetMapping("/questions/modify")
    public String questionsModify(@RequestParam Long id, Model model) {
        Optional<Question> question = dbServiceQuestion.findQuestionById(id);
        List<Level> levels = dbServiceLevel.findAll();
        List<Topic> topics = dbServiceTopic.findAll();

        model.addAttribute("question", question.get());
        model.addAttribute("levels", levels);
        model.addAttribute("topics", topics);

        return "questionModify";
    }

    @PostMapping("/questions/save")
    public RedirectView questionsSave(QuestionDto questionDto) {
        log.info("Question to be updated: {}", questionDto);

        Question question = new Question(questionDto.id(), questionDto.levelId(), questionDto.topicId(), questionDto.question());
        dbServiceQuestion.saveQuestion(question);

        //dbServiceQuestion.updateQuestion(questionDto.id(), questionDto.levelId(), questionDto.topicId(), questionDto.question());
        return new RedirectView("/questions", true);
    }

    @GetMapping("/questions/delete")
    public RedirectView questionsDelete(@RequestParam Long id) {
        log.info("Question to be deleted: {}", id);

        dbServiceQuestion.deleteQuestion(id);

        return new RedirectView("/questions", true);
    }

    @GetMapping("/topics/modify")
    public String topicModify(@RequestParam Long id, Model model) {
        Optional<Topic> topic = dbServiceTopic.findTopicById(id);

        model.addAttribute("topic", topic.get());

        return "topicModify";
    }

    @PostMapping("/topics/save")
    public RedirectView topicSave(TopicDto topicDto) {
        log.info("Topic to be updated: {}", topicDto);

        Topic topic = new Topic(topicDto.id(), topicDto.topic(), null);
        dbServiceTopic.saveTopic(topic);

        return new RedirectView("/questions", true);
    }

    @GetMapping("/topics/delete")
    public RedirectView topicDelete(@RequestParam Long id) {
        log.info("Topic to be deleted: {}", id);

        dbServiceTopic.deleteTopicById(id);

        return new RedirectView("/questions", true);
    }

    @GetMapping("/levels/modify")
    public String levelModify(@RequestParam Long id, Model model) {
        Optional<Level> level = dbServiceLevel.findLevelById(id);

        model.addAttribute("level", level.get());

        return "levelModify";
    }

    @PostMapping("/levels/save")
    public RedirectView levelSave(LevelDto levelDto) {
        log.info("Level to be updated: {}", levelDto);

        Level level = new Level(levelDto.id(), levelDto.name(), levelDto.description(), null);
        dbServiceLevel.saveLevel(level);

        return new RedirectView("/questions", true);
    }

    @GetMapping("/levels/delete")
    public RedirectView levelDelete(@RequestParam Long id) {
        log.info("level to be deleted: {}", id);

        dbServiceLevel.deleteLevelById(id);

        return new RedirectView("/questions", true);
    }

}
