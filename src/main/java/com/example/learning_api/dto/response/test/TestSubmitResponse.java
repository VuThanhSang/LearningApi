package com.example.learning_api.dto.response.test;

import com.example.learning_api.dto.response.answer.CreateAnswerResponse;
import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import com.example.learning_api.entity.sql.database.QuestionEntity;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TestSubmitResponse {
    private String id;
    private String studentId;
    private double grade;
    private int totalCorrectAnswers;
    private int totalQuestions;
    private String testId;
    private String testType;
    private boolean isPassed;
    private String attendedAt;
    private String finishedAt;
    private List<QuestionResponse> questions;
    @Data
    public static class QuestionResponse {
        private String id;
        private String content;
        private String description;
        private String source;
        private String type;

        private List<AnswerResponse> answers;
        public static QuestionResponse formQuestionEntity(QuestionEntity questionEntity){
            QuestionResponse questionResponse = new QuestionResponse();
            questionResponse.setId(questionEntity.getId());
            questionResponse.setContent(questionEntity.getContent());
            questionResponse.setDescription(questionEntity.getDescription());
            questionResponse.setSource(questionEntity.getSource());
            questionResponse.setType(questionEntity.getType().name());
            return questionResponse;
        }

    }
    @Data
    public static class AnswerResponse {
        private String id;
        private String content;
        private boolean isCorrect;
        private boolean isSelected;
        private String questionId;
        private String source;
        public static AnswerResponse formAnswerEntity(CreateAnswerResponse answerEntity){
            AnswerResponse answerResponse = new AnswerResponse();
            answerResponse.setId(answerEntity.getId());
            answerResponse.setContent(answerEntity.getContent());
            answerResponse.setCorrect(answerEntity.isCorrect());
            answerResponse.setQuestionId(answerEntity.getQuestionId());
            answerResponse.setSource(answerEntity.getSource());
            return answerResponse;
        }
    }

}
