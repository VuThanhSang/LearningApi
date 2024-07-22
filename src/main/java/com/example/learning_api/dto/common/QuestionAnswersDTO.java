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
    public static TestSubmitResponse.QuestionResponse formQuestionEntity(QuestionEntity questionEntity){
        TestSubmitResponse.QuestionResponse questionResponse = new TestSubmitResponse.QuestionResponse();
        questionResponse.setId(questionEntity.getId());
        questionResponse.setContent(questionEntity.getContent());
        questionResponse.setDescription(questionEntity.getDescription());
        questionResponse.setSource(questionEntity.getSource());
        questionResponse.setType(questionEntity.getType().name());
        return questionResponse;
    }
}
