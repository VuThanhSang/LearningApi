package com.example.learning_api.dto.common;

import com.example.learning_api.dto.response.test.TestSubmitResponse;
import com.example.learning_api.entity.sql.database.QuestionEntity;
import lombok.Data;

import java.util.List;

@Data
public class QuestionAnswersDTO {
    private String id;
    private String content;
    private String description;
    private String source;
    private String type;

    private List<TestSubmitResponse.AnswerResponse> answers;

}
