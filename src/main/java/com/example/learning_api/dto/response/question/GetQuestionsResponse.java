package com.example.learning_api.dto.response.question;

import com.example.learning_api.dto.response.answer.CreateAnswerResponse;
import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.entity.sql.database.QuestionEntity;
import lombok.Data;

import java.util.List;
@Data
public class GetQuestionsResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<QuestionResponse> questions;
    @Data
    public static class QuestionResponse {
        private String id;
        private String content;
        private String description;
        private Integer index;
        private List<FileEntity> sources;
        private String type;
        private String createdAt;
        private String updatedAt;

        private List<AnswerResponse> answers;


    }
    @Data
    public static class AnswerResponse {
        private String id;
        private String content;
        private Boolean isCorrect;
        private String questionId;
        private FileEntity source;
        private boolean isSelected;
        private String createdAt;
        private String updatedAt;
        private String answerText;

        public AnswerResponse withoutIsCorrect(){
            AnswerResponse answerResponse = new AnswerResponse();
            answerResponse.setId(this.id);
            answerResponse.setContent(this.content);
            answerResponse.setQuestionId(this.questionId);
            answerResponse.setSource(this.source);
            answerResponse.setCreatedAt(this.createdAt);
            answerResponse.setUpdatedAt(this.updatedAt);
            return answerResponse;
        }
    }

}
