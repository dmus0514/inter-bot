package com.dmus.controllers;

import com.dmus.dto.InterviewDto;
import com.dmus.model.*;
import com.dmus.service.DBServiceInterview;
import com.dmus.service.DBServiceLevel;
import com.dmus.service.DBServiceUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
public class InterviewsController {

    private final DBServiceInterview dbServiceInterview;
    private final DBServiceUser dbServiceUser;
    private final DBServiceLevel dbServiceLevel;

    public InterviewsController(DBServiceInterview dbServiceInterview,
                                DBServiceUser dbServiceUser,
                                DBServiceLevel dbServiceLevel) {
        this.dbServiceInterview = dbServiceInterview;
        this.dbServiceUser = dbServiceUser;
        this.dbServiceLevel = dbServiceLevel;
    }

    @GetMapping("/interviews")
    public String interviewsListView(Model model) {
        List<InterviewDto> interviews = dbServiceInterview.getLastFiveInterviews();
        List<User> users = dbServiceUser.findAll();
        List<Level> levels = dbServiceLevel.findAll();
        log.info("interviewsListView > interviews: {}", interviews);

        model.addAttribute("interviews", interviews);
        model.addAttribute("users", users);
        model.addAttribute("levels", levels);
        if (!CollectionUtils.isEmpty(interviews)) {
            model.addAttribute("textToPrint", "Последние 5 собеседований:");
        }

        return "interviewlist";
    }

    @GetMapping("/interviews/search")
    public String findInterviews(@RequestParam(required = false) Long userId,
                                     @RequestParam(required = false) LocalDate date,
                                     @RequestParam(required = false) Long levelId,
                                     Model model) {
        log.info("findInterviews > userId: {}, date: {}, levelId: {}", userId, date, levelId);

        List<InterviewDto> interviews = dbServiceInterview.findInterviewsByDateLevelUser (date, levelId, userId);
        List<User> users = dbServiceUser.findAll();
        List<Level> levels = dbServiceLevel.findAll();

        model.addAttribute("interviews", interviews);
        model.addAttribute("users", users);
        model.addAttribute("levels", levels);
        if (!CollectionUtils.isEmpty(interviews)) {
            model.addAttribute("textToPrint", "Результаты поиска:");
        }
        log.info("findInterviews > interviews: {}", interviews);

        return "interviewlist";
    }
}
