package com.example.learning_api.dto.response.question;

import com.example.learning_api.dto.response.answer.CreateAnswerResponse;
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
        private String source;
        private String type;
        private String createdAt;
        private String updatedAt;
        private List<AnswerResponse> answers;
        public static QuestionResponse formQuestionEntity(QuestionEntity questionEntity){
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setId(questionEntity.getId());
            questionResponse.setContent(questionEntity.getContent());
            questionResponse.setDescription(questionEntity.getDescription());
            questionResponse.setSource(questionEntity.getSource());
            questionResponse.setType(questionEntity.getType().name());
            questionResponse.setCreatedAt(questionEntity.getCreatedAt().toString());
            questionResponse.setUpdatedAt(questionEntity.getUpdatedAt().toString());
            return questionResponse;
        }

    }
    @Data
    public static class AnswerResponse {
        private String id;
        private String content;
        private Boolean isCorrect;
        private String questionId;
        private String source;
        private boolean isSelected;
        private String createdAt;
        private String updatedAt;
        public static AnswerResponse formAnswerEntity(CreateAnswerResponse answerEntity){
            AnswerResponse answerResponse = new AnswerResponse();
            answerResponse.setId(answerEntity.getId());
            answerResponse.setContent(answerEntity.getContent());
            answerResponse.setIsCorrect(answerEntity.isCorrect());
            answerResponse.setQuestionId(answerEntity.getQuestionId());
            answerResponse.setSource(answerEntity.getSource());
            answerResponse.setCreatedAt(answerEntity.getCreatedAt().toString());
            answerResponse.setUpdatedAt(answerEntity.getUpdatedAt().toString());
            return answerResponse;
        }
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
