package com.example.learning_api.repository.database;

import com.example.learning_api.entity.sql.database.DeadlineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface DeadlineRepository extends MongoRepository<DeadlineEntity, String> {
    Page<DeadlineEntity> findAllByLessonId(String lessonId, Pageable pageable);

    @Query("{'teacherId': ?0, " +
            "$and: [" +
            "   {'title': {$regex: ?1, $options: 'i'}}," +
            "   {$or: [" +
            "       {$expr: {$eq: [?2, null]}}, " +
            "       {'status': ?2}" +
            "   ]}," +
            "   {$or: [" +
            "       {$expr: {$eq: [?3, null]}}, " +
            "       {'startDate': {$gte: ?3}}" +
            "   ]}," +
            "   {$or: [" +
            "       {$expr: {$eq: [?4, null]}}, " +
            "       {'endDate': {$lte: ?4}}" +
            "   ]}" +
            "]}")
    Page<DeadlineEntity> findByTeacherIdWithFilters(String teacherId,
                                                    String search,
                                                    String status,
                                                    String startDate,
                                                    String endDate,
                                                    Pageable pageable);
    @Query("{'classroomId': {$in: db.student_enrollments.find({'studentId': ?0}).map(e => e.classroomId)}, " +
            "$and: [" +
            "   {'title': {$regex: ?1, $options: 'i'}}," +
            "   {$or: [" +
            "       {$expr: {$eq: [?2, null]}}, " +
            "       {'status': ?2}" +
            "   ]}," +
            "   {$or: [" +
            "       {$expr: {$eq: [?3, null]}}, " +
            "       {'startDate': {$gte: ?3}}" +
            "   ]}," +
            "   {$or: [" +
            "       {$expr: {$eq: [?4, null]}}, " +
            "       {'endDate': {$lte: ?4}}" +
            "   ]}," +
            "   {$or: [" +
            "       {$expr: {$eq: [?5, null]}}, " +
            "       {'classroomId': ?5}" +
            "   ]}" +
            "]}")
    Page<DeadlineEntity> findByStudentIdWithFilters(String studentId,
                                                    String search,
                                                    String status,
                                                    String startDate,
                                                    String endDate,
                                                    String classroomId,
                                                    Pageable pageable);
}