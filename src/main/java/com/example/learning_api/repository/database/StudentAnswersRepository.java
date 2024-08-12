package com.example.learning_api.repository.database;

import com.example.learning_api.dto.common.QuestionAnswersDTO;
import com.example.learning_api.entity.sql.database.StudentAnswersEntity;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface StudentAnswersRepository extends MongoRepository<StudentAnswersEntity, String> {
    void deleteByQuestionId(String questionId);
    void deleteByStudentId(String studentId);
    void deleteByTestResultId(String testResultId);
    @Query("{ studentId: ?0, testResultId: ?1, questionId: ?2 , answerId: ?3}")
    StudentAnswersEntity findByStudentIdAndTestResultIdAndQuestionIdAndAnswerId(String studentId, String testResultId, String questionId, String answerId);
    @Aggregation(pipeline = {
            "{ $addFields: { _answerId: { $cond: { if: { $ne: ['$answerId', null] }, then: { $toObjectId: '$answerId' }, else: null } } } }",
            "{ $match: { studentId: ?0, testId: ?1 } }",
            "{ $lookup: { from: 'answers', localField: '_answerId', foreignField: '_id', as: 'answerDetails' } }",
            "{ $group: { _id: '$questionId', answers: { $push: { answerId: '$_answerId', content: { $arrayElemAt: ['$answerDetails.content', 0] } } } } }",
            "{ $project: { _id: 0, questionId: { $toString: '$_id' }, answers: 1 } }"
    })
    List<QuestionAnswersDTO> getStudentAnswers(String studentId, String testResultId);


    @Query("{ studentId: ?0, testResultId: { $in: ?1 } }")
    List<StudentAnswersEntity> findByStudentIdAndTestResultIdIn(String studentId, List<String> testResultIds);
    @Query("{ studentId: ?0, testResultId: ?1, }")
    List<StudentAnswersEntity> findByStudentIdAndTestResultId(String studentId, String testResultId);

    int countByStudentIdAndTestResultIdAndAnswerId(String studentId, String testResultId, String answerId);
}
