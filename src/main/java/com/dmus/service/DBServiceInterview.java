package com.dmus.service;

import com.dmus.dto.InterviewDto;
import com.dmus.model.Interview;
import com.dmus.repository.InterviewsRepository;
import com.dmus.repository.LevelsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public class DBServiceInterview {

    private final InterviewsRepository interviewsRepository;
    private final LevelsRepository levelsRepository;

    public DBServiceInterview(InterviewsRepository interviewsRepository, LevelsRepository levelsRepository) {
        this.interviewsRepository = interviewsRepository;
        this.levelsRepository = levelsRepository;
    }

    public List<Interview> findInterviewsByDate(LocalDate localDate) {
        return interviewsRepository.findInterviewsByDateBetween(localDate.atStartOfDay(), localDate.atTime(LocalTime.MAX));
    }

    public List<Interview> findInterviewsByLevel(Long levelId) {
        return interviewsRepository.findInterviewsByLevelId(levelId);
    }

    public List<Interview> findInterviewsByUser(Long userId) {
        return interviewsRepository.findInterviewsByUserId(userId);
    }

    public List<InterviewDto> findInterviewsByDateLevelUser(LocalDate localDate, Long levelId, Long userId) {
        LocalDateTime startDate;
        LocalDateTime endDate;
        if (localDate == null) { //if date is not set initially then set it to edge values to make the search work
            startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
            endDate = LocalDateTime.of(2038, 1, 19, 3, 14); //end of epoch
        } else {
            startDate = localDate.atStartOfDay();
            endDate = localDate.atTime(LocalTime.MAX);
        }

        if (userId == null) {
            if (levelId == null) {
                return interviewsRepository.findInterviewsByDateBetweenCustom(startDate, endDate);
            } else {
                return interviewsRepository.findInterviewsByDateBetweenAndLevelIdCustom(startDate, endDate, levelId);
            }
        } else {
            if (levelId == null) {
                return interviewsRepository.findInterviewsByDateBetweenAndUserIdCustom(startDate, endDate, userId);
            } else {
                return interviewsRepository.findInterviewsByDateBetweenAndLevelIdAndUserIdCustom(startDate, endDate, levelId, userId);
            }
        }
    }

    public List<InterviewDto> getLastFiveInterviews() {
        return interviewsRepository.getLastFiveInterviews();
    }

    /*private Long getLongDate(LocalDate localDate, boolean isStartOfDay) {
        LocalDateTime localDateTime = isStartOfDay ? localDate.atStartOfDay() : localDate.atTime(LocalTime.MAX);
        return localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }*/
}
