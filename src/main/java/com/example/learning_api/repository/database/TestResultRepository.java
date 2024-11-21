package com.example.learning_api.repository.database;

import com.example.learning_api.dto.response.test.ScoreDistributionResponse;
import com.example.learning_api.dto.response.test.TestResultOfTestResponse;
import com.example.learning_api.entity.sql.database.TestResultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Aggregation(pipeline = {
            "{ $match: { testId: ?0, state: 'FINISHED' } }",
            "{ $sort: { grade: -1 } }",
            "{ $group: { " +
                    "_id: '$studentId', " +
                    "maxGrade: { $first: '$grade' }, " +
                    "resultId: { $first: '$_id' }, " +
                    "isPassed: { $first: '$isPassed' }, " +
                    "attendedAt: { $first: '$attendedAt' }, " +
                    "finishedAt: { $first: '$finishedAt' }, " +
                    "state: { $first: '$state' } " +
                    "} }",
            "{ $lookup: { " +
                    "from: 'student', " +
                    "localField: '_id', " +
                    "foreignField: '_id', " +
                    "as: 'studentInfo' " +
                    "} }",
            "{ $unwind: { path: '$studentInfo', preserveNullAndEmptyArrays: true } }",
            "{ $lookup: { " +
                    "from: 'user', " +
                    "localField: 'studentInfo.userId', " +
                    "foreignField: '_id', " +
                    "as: 'userInfo' " +
                    "} }",
            "{ $unwind: { path: '$userInfo', preserveNullAndEmptyArrays: true } }",
            "{ $match: { " +
                    "$and: [ " +
                    "{ $or: [ { 'userInfo.fullname': { $regex: ?1, $options: 'i' } }, { $expr: { $eq: [?1, ''] } } ] }, " +
                    "{ $or: [ { maxGrade: { $gte: ?2 } }, { $expr: { $eq: [?2, null] } } ] }, " +
                    "{ $or: [ { maxGrade: { $lte: ?3 } }, { $expr: { $eq: [?3, null] } } ] }, " +
                    "{ $or: [ { isPassed: ?4 }, { $expr: { $eq: [?4, null] } } ] } " +
                    "] " +
                    "} }",
            "{ $project: { " +
                    "_id: 0, " +
                    "studentId: '$_id', " +
                    "testId: ?0, " +
                    "grade: '$maxGrade', " +
                    "resultId: 1, " +
                    "isPassed: 1, " +
                    "attendedAt: 1, " +
                    "finishedAt: 1, " +
                    "state: 1, " +
                    "fullname: { $ifNull: ['$userInfo.fullname', ''] }, " +
                    "email: { $ifNull: ['$userInfo.email', ''] }, " +
                    "phone: { $ifNull: ['$studentInfo.phone', ''] } " +
                    "} }",
            "{ $sort: { grade: 1 } }"
    })
    List<TestResultOfTestResponse> findHighestGradesByTestIdAndFilters(String testId, String fullname, Integer minGrade, Integer maxGrade, Boolean passed);
    @Aggregation(pipeline = {
            "{ $match: { testId: ?0, state: 'FINISHED' } }",
            "{ $group: { _id: '$studentId' } }",
            "{ $count: 'total' }"
    })
    long countDistinctStudentsByTestId(String testId);

//    @Aggregation(pipeline = {
//            "{ $match: { testId: ?0, state: 'FINISHED' } }",
//            "{ $group: { " +
//                    "_id: '$studentId', " +
//                    "maxGrade: { $max: '$grade' } " +
//                    "} }",
//            "{ $bucket: { " +
//                    "groupBy: '$maxGrade', " +
//                    "boundaries: [-1,0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11], " +
//                    "default: 'Other', " +
//                    "output: { " +
//                    "count: { $sum: 1 }, " +
//                    "students: { $push: '$_id' } " +
//                    "} " +
//                    "} }",
//            "{ $sort: { _id: 1 } }"
//    })
//    List<ScoreDistributionResponse> getScoreDistribution(String testId);
        @Aggregation(pipeline = {
                "{ $match: { studentId: ?0 } }",
                "{$addFields: {_testId: {$toObjectId: '$testId'}}}",
                "{ $lookup: { " +
                        "from: 'test', " +
                        "localField: '_testId', " +
                        "foreignField: '_id', " +
                        "as: 'testInfo' " +
                        "} }",
                "{ $unwind: '$testInfo' }",
                "{ $match: { 'testInfo.classroomId': ?1 } }",
                "{ $project: { " +
                        "_id: 0, " +
                        "studentId: 1, " +
                        "testId: 1, " +
                        "grade: 1, " +
                        "resultId: '$_id', " +
                        "isPassed: 1, " +
                        "attendedAt: 1, " +
                        "finishedAt: 1, " +
                        "state: 1, " +
                        "testInfo: 1 " +
                        "} }"
        })
        List<TestResultOfTestResponse> findByStudentIdAndClassroomId(String studentId, String classroomId);
}