package com.dmus.repository;

import com.dmus.dto.InterviewDto;
import com.dmus.dto.Question;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class InterviewResultSetExtractor implements ResultSetExtractor<List<InterviewDto>> {
    @Override
    public List<InterviewDto> extractData(ResultSet rs) throws SQLException, DataAccessException {
        var interviews = new ArrayList<InterviewDto>();
        Long prevInterviewId = null;
        InterviewDto interviewDto = null;
        while (rs.next()) {
            var interviewId = (Long) rs.getObject("interview_id");
            if (prevInterviewId == null || !prevInterviewId.equals(interviewId)) {
                interviewDto = new InterviewDto(interviewId, rs.getString("user_name"),
                        ((Timestamp) rs.getObject("interview_date")).toLocalDateTime(), rs.getString("level_name"),
                        rs.getString("interview_feedback"), (Integer) rs.getObject("interview_grade"),
                        new HashSet<>());
                interviews.add(interviewDto);
                prevInterviewId = interviewId;
            }
            if (interviewDto != null) {
                interviewDto.askedQuestions().add(new Question(rs.getString("question"), rs.getString("answer")));
            }
        }
        return interviews;
    }
}
