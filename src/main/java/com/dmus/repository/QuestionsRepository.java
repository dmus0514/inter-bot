package com.dmus.repository;

import com.dmus.dto.QuestionDto;
import com.dmus.model.Question;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionsRepository extends ListCrudRepository<Question, Long> {
    @Query(value = """
            SELECT
                q.question,
                l.name,
                t.topic
            FROM questions q
                     LEFT OUTER JOIN levels l ON q.level_id = l.id
                     LEFT OUTER JOIN topics t ON q.topic_id = t.id
            """)
    List<Question> findAllExt();

    @Query(value = "SELECT * FROM questions ORDER BY id")
    List<Question> findAllSorted();

    @Modifying
    @Query(value = "update questions set level_id = :levelId, topic_id = :topicId, question = :questionText where id = :questionId")
    void updateQuestion(@Param("questionId") Long questionId, @Param("levelId") Long levelId, @Param("topicId") Long topicId, @Param("questionText") String questionText);
}
