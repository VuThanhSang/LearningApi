package com.example.learning_api.repository.database;

import com.example.learning_api.dto.response.test.TestResultOfTestResponse;
import com.example.learning_api.entity.sql.database.TestResultEntity;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TestResultRepository extends MongoRepository<TestResultEntity, String> {
    @Query(value = "{studentId: ?0, testId: ?1, state: 'FINISHED'}")
    List<TestResultEntity> findByStudentIdAndTestId(String studentId, String testId);

    void deleteByStudentIdAndTestId(String studentId, String testId);
    void deleteByTestId(String testId);
    @Query(value = "{studentId: ?0, testId: ?1}", count = true)
    int countByStudentIdAndTestId(String studentId, String testId);
    TestResultEntity findFirstByStudentIdAndTestIdAndStateOrderByAttendedAtDesc(String studentId, String testId, String state);
    @Aggregation(pipeline = {
            "{ $match: { testId: ?0, state: 'FINISHED' } }",
            "{ $sort: { grade: -1 } }", // Giữ nguyên sắp xếp giảm dần để lấy điểm cao nhất
            "{ $group: { " +
                    "_id: '$studentId', " +
                    "maxGrade: { $first: '$grade' }, " + // Giữ nguyên maxGrade
                    "resultId: { $first: '$_id' }, " +
                    "isPassed: { $first: '$isPassed' }, " +
                    "attendedAt: { $first: '$attendedAt' }, " +
                    "finishedAt: { $first: '$finishedAt' }, " +
                    "state: { $first: '$state' } " +
                    "} }",
            "{ $lookup: { " +
                    "from: 'test', " +
                    "let: { testId: ?0 }, " +
                    "pipeline: [ " +
                    "{ $match: { $expr: { $eq: [{ $toString: '$_id' }, '$$testId'] } } }, " +
                    "{ $project: { name: 1, description: 1, duration: 1, classroomId: 1, teacherId: 1 } } " +
                    "], " +
                    "as: 'testInfo' " +
                    "} }",
            "{ $unwind: '$testInfo' }",
            "{ $project: { " +
                    "_id: 0, " +
                    "studentId: '$_id', " +
                    "testId: ?0, " +
                    "grade: '$maxGrade', " + // Đổi tên từ maxGrade thành grade trong kết quả cuối cùng
                    "resultId: 1, " +
                    "isPassed: 1, " +
                    "attendedAt: 1, " +
                    "finishedAt: 1, " +
                    "state: 1, " +
                    "testInfo: 1 " +
                    "} }",
            "{ $sort: { grade: 1 } }" // Sắp xếp kết quả cuối cùng theo grade tăng dần
    })
    List<TestResultOfTestResponse> findHighestGradesByTestIdAndFinishedStateSortedAscending(String testId);
}