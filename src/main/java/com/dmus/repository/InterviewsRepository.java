package com.dmus.repository;

import com.dmus.dto.InterviewDto;
import com.dmus.model.Interview;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InterviewsRepository extends ListCrudRepository<Interview, Long> {

    List<Interview> findInterviewsByDateBetween(LocalDateTime dateAfter, LocalDateTime dateBefore);
    List<Interview> findInterviewsByLevelId(Long levelId);
    List<Interview> findInterviewsByUserId(Long userId);

    List<Interview> findInterviewsByDateBetweenAndLevelIdAndUserId(LocalDateTime dateAfter, LocalDateTime dateBefore, Long levelId, Long userId);
    List<Interview> findInterviewsByDateBetweenAndLevelId(LocalDateTime dateAfter, LocalDateTime dateBefore, Long levelId);

    @Query(value = """
            select i.id as interview_id,
                   u.tg_username as user_name,
                   i.date as interview_date,
                   l.name as level_name,
                   i.feedback as interview_feedback,
                   i.result_grade as interview_grade,
                   q.question_text as question,
                   q.answer_text as answer
            from interviews i
                left outer join users u on u.id = i.user_id
                left outer join levels l on l.id = i.level_id
                left outer join asked_questions q on q.interview_id = i.id
            where i.date between :startDate AND :endDate AND (i.level_id = :levelId) AND (i.user_id = :userId)
            order by i.id
            """, resultSetExtractorClass = InterviewResultSetExtractor.class)
    List<InterviewDto> findInterviewsByDateBetweenAndLevelIdAndUserIdCustom(@Param("startDate") LocalDateTime startDate,
                                                                            @Param("endDate") LocalDateTime endDate,
                                                                            @Param("levelId") Long levelId,
                                                                            @Param("userId") Long userId);

    @Query(value = """
            select i.id as interview_id,
                   u.tg_username as user_name,
                   i.date as interview_date,
                   l.name as level_name,
                   i.feedback as interview_feedback,
                   i.result_grade as interview_grade,
                   q.question_text as question,
                   q.answer_text as answer
            from interviews i
                left outer join users u on u.id = i.user_id
                left outer join levels l on l.id = i.level_id
                left outer join asked_questions q on q.interview_id = i.id
            where i.date between :startDate AND :endDate AND (i.user_id = :userId)
            order by i.id
            """, resultSetExtractorClass = InterviewResultSetExtractor.class)
    List<InterviewDto> findInterviewsByDateBetweenAndUserIdCustom(@Param("startDate") LocalDateTime startDate,
                                                                            @Param("endDate") LocalDateTime endDate,
                                                                            @Param("userId") Long userId);

    @Query(value = """
            select i.id as interview_id,
                   u.tg_username as user_name,
                   i.date as interview_date,
                   l.name as level_name,
                   i.feedback as interview_feedback,
                   i.result_grade as interview_grade,
                   q.question_text as question,
                   q.answer_text as answer
            from interviews i
                left outer join users u on u.id = i.user_id
                left outer join levels l on l.id = i.level_id
                left outer join asked_questions q on q.interview_id = i.id
            where i.date between :startDate AND :endDate AND (i.level_id = :levelId)
            order by i.id
            """, resultSetExtractorClass = InterviewResultSetExtractor.class)
    List<InterviewDto> findInterviewsByDateBetweenAndLevelIdCustom(@Param("startDate") LocalDateTime startDate,
                                                                  @Param("endDate") LocalDateTime endDate,
                                                                  @Param("levelId") Long levelId);

    @Query(value = """
            select i.id as interview_id,
                   u.tg_username as user_name,
                   i.date as interview_date,
                   l.name as level_name,
                   i.feedback as interview_feedback,
                   i.result_grade as interview_grade,
                   q.question_text as question,
                   q.answer_text as answer
            from interviews i
                left outer join users u on u.id = i.user_id
                left outer join levels l on l.id = i.level_id
                left outer join asked_questions q on q.interview_id = i.id
            where i.date between :startDate AND :endDate
            order by i.id
            """, resultSetExtractorClass = InterviewResultSetExtractor.class)
    List<InterviewDto> findInterviewsByDateBetweenCustom(@Param("startDate") LocalDateTime startDate,
                                                                   @Param("endDate") LocalDateTime endDate);

    @Query(value = """
            select i.id as interview_id,
                   u.tg_username as user_name,
                   i.date as interview_date,
                   l.name as level_name,
                   i.feedback as interview_feedback,
                   i.result_grade as interview_grade,
                   q.question_text as question,
                   q.answer_text as answer
            from interviews i
            JOIN (
                SELECT DISTINCT id, date
                FROM interviews
                ORDER BY date DESC
                LIMIT 5
            ) recent_interviews ON i.id = recent_interviews.id
            LEFT OUTER JOIN users u on u.id = i.user_id
            LEFT OUTER JOIN levels l on l.id = i.level_id
            LEFT OUTER JOIN asked_questions q on q.interview_id = i.id
            ORDER BY i.date DESC
            """, resultSetExtractorClass = InterviewResultSetExtractor.class)
    List<InterviewDto> getLastFiveInterviews();
}
