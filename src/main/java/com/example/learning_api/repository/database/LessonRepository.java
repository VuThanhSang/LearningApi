package com.example.learning_api.repository.database;


import com.example.learning_api.dto.response.lesson.GetLessonDetailResponse;
import com.example.learning_api.entity.sql.database.LessonEntity;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LessonRepository extends MongoRepository<LessonEntity, String>{
    @Aggregation(pipeline = {
            "{ '$match': { '_id': ObjectId(?0) } }",
            "{ '$addFields': { '_idString': { '$toString': '$_id' } } }",
            "{ '$lookup': { 'from': 'resources', 'localField': '_idString', 'foreignField': 'lessonId', 'as': 'resources' } }",
            "{ '$lookup': { 'from': 'media', 'localField': '_idString', 'foreignField': 'lessonId', 'as': 'media' } }",
            "{ '$lookup': { 'from': 'substances', 'localField': '_idString', 'foreignField': 'lessonId', 'as': 'substances' } }",
            "{ '$lookup': { 'from': 'deadlines', 'localField': '_idString', 'foreignField': 'lessonId', 'as': 'deadlines' } }",
    })
    GetLessonDetailResponse getLessonWithResourcesAndMediaAndSubstances(String id);
    @Query( "{ 'sectionId': ?0, 'status': { $in: ?1 } }")
    List<LessonEntity> findBySectionId(String sectionId,org.springframework.data.domain.Sort sort,List<String> statuses);
    @Aggregation(pipeline = {
            "{ '$match': { 'sectionId': ?0 } }",
            "{ '$group': { '_id': null, 'maxIndex': { '$max': '$index' } } }"
    })
    Integer findMaxIndexBySectionId(String sectionId);
}
