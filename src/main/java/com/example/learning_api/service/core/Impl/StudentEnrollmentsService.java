package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.student_enrollments.EnrollStudentRequest;
import com.example.learning_api.dto.response.classroom.GetStudentInClassResponse;
import com.example.learning_api.dto.response.student.StudentsResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.StudentEnrollmentsEntity;
import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.enums.StudentEnrollmentStatus;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.core.IStudentEnrollmentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentEnrollmentsService implements IStudentEnrollmentsService {
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final ClassRoomRepository classroomRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void enrollStudent(EnrollStudentRequest body) {
        try {
            if (body.getStudentId() == null) {
                throw new IllegalArgumentException("StudentId is required");
            }
             if (studentRepository.findById(body.getStudentId()).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            ClassRoomEntity classRoomEntity = classroomRepository.findById(body.getClassroomId()).orElse(null);
            if (classRoomEntity == null) {
                throw new IllegalArgumentException("ClassroomId is not found");
            }
            if (classRoomEntity.getEnrollmentCapacity() <= classRoomEntity.getCurrentEnrollment()) {
                throw new IllegalArgumentException("Classroom is full");
            }

            StudentEnrollmentsEntity studentEnrollmentsEntity = new StudentEnrollmentsEntity();
            studentEnrollmentsEntity.setStudentId(body.getStudentId());
            studentEnrollmentsEntity.setClassroomId(body.getClassroomId());
            studentEnrollmentsEntity.setGrade("0");
            studentEnrollmentsEntity.setEnrolledAt(String.valueOf(System.currentTimeMillis()));
            studentEnrollmentsEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            studentEnrollmentsEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            studentEnrollmentsEntity.setStatus(StudentEnrollmentStatus.IN_PROGRESS);
            classRoomEntity.setCurrentEnrollment(classRoomEntity.getCurrentEnrollment() + 1);
            classroomRepository.save(classRoomEntity);
            studentEnrollmentsRepository.save(studentEnrollmentsEntity);

        } catch (Exception e) {

            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void dropStudent(String studentId, String courseId) {
        try {
            if (studentId == null) {
                throw new IllegalArgumentException("StudentId is required");
            }
            if (courseId == null) {
                throw new IllegalArgumentException("CourseId is required");
            }
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            if (courseRepository.findById(courseId).isEmpty()) {
                throw new IllegalArgumentException("CourseId is not found");
            }
//            studentEnrollmentsRepository.deleteByStudentIdAndCourseId(studentId, courseId);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public void completeStudent(String studentId, String courseId) {
        try {
            if (studentId == null) {
                throw new IllegalArgumentException("StudentId is required");
            }
            if (courseId == null) {
                throw new IllegalArgumentException("CourseId is required");
            }
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            if (courseRepository.findById(courseId).isEmpty()) {
                throw new IllegalArgumentException("CourseId is not found");
            }
            StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsRepository.findByStudentIdAndCourseId(studentId, courseId);
            studentEnrollmentsEntity.setStatus(StudentEnrollmentStatus.COMPLETED);
            studentEnrollmentsEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            studentEnrollmentsRepository.save(studentEnrollmentsEntity);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }



    @Override
    public void updateStudentGrade(String studentId, String courseId, int grade) {
        // TODO Auto-generated method stub
        try {
            if (studentId == null) {
                throw new IllegalArgumentException("StudentId is required");
            }
            if (courseId == null) {
                throw new IllegalArgumentException("CourseId is required");
            }
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            if (courseRepository.findById(courseId).isEmpty()) {
                throw new IllegalArgumentException("CourseId is not found");
            }
            StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsRepository.findByStudentIdAndCourseId(studentId, courseId);
            studentEnrollmentsEntity.setGrade(String.valueOf(grade));
            studentEnrollmentsEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            studentEnrollmentsRepository.save(studentEnrollmentsEntity);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetStudentInClassResponse getStudentInClass(String classroomId, Integer page, Integer limit, String search, String sort, String order) {
        try {
            // Validate classroom exists (you might want to add a classroom repository check)

            // Clean up and validate search parameter
            search = (search == null) ? "" : search;

            // Validate and sanitize sort parameter
            List<String> allowedSortFields = Arrays.asList("id", "userId", "fullname", "gradeLevel");
            if (sort == null || !allowedSortFields.contains(sort)) {
                sort = "fullname"; // default sort
            }

            // Validate pagination parameters
            page = (page == null || page < 0) ? 0 : page;
            limit = (limit == null || limit <= 0) ? 10 : limit; // default limit

            // Fetch students enrolled in the classroom
            List<StudentEnrollmentsEntity> enrollments = studentEnrollmentsRepository
                    .findByClassroomIdAndStatus(classroomId, StudentEnrollmentStatus.IN_PROGRESS);

            // Collect student IDs from enrollments
            List<String> studentIds = enrollments.stream()
                    .map(StudentEnrollmentsEntity::getStudentId)
                    .collect(Collectors.toList());

            // Fetch students with filtering
            List<StudentEntity> allStudents = studentRepository.findByIdInAndSearch(studentIds, search);

            // Sort students
            Comparator<StudentEntity> comparator;
            switch (sort) {
                case "id":
                    comparator = Comparator.comparing(StudentEntity::getId);
                    break;
                case "userId":
                    comparator = Comparator.comparing(StudentEntity::getUserId);
                    break;
                case "gradeLevel":
                    comparator = Comparator.comparing(StudentEntity::getGradeLevel);
                    break;
                default: // fullname
                    comparator = Comparator.comparing(student ->
                            student.getUser() != null ? student.getUser().getFullname() : ""
                    );
            }

            // Apply sorting direction
            if ("desc".equalsIgnoreCase(order)) {
                comparator = comparator.reversed();
            }

            // Sort the students
            allStudents.sort(comparator);

            // Paginate the results
            int start = page * limit;
            int end = Math.min((start + limit), allStudents.size());
            List<StudentEntity> paginatedStudents = allStudents.subList(start, end);

            // Convert to response DTOs
            List<GetStudentInClassResponse.StudentResponse> studentResponses = paginatedStudents.stream()
                    .map(GetStudentInClassResponse.StudentResponse::formStudentEntity)
                    .collect(Collectors.toList());

            // Prepare the response
            GetStudentInClassResponse response = new GetStudentInClassResponse();
            response.setStudents(studentResponses);
            response.setTotalElements((long) allStudents.size());
            response.setTotalPage((int) Math.ceil((double) allStudents.size() / limit));

            return response;
        } catch (Exception e) {
            log.error("Error in getStudentInClass: ", e);
            throw new IllegalArgumentException("Error retrieving students in class: " + e.getMessage());
        }
    }

    @Override
    public StudentsResponse getStudents(Integer page, Integer limit, String search, String sort, String order, String classroomId) {
        try {
            // Clean up and validate search parameter
            search = (search == null) ? "" : search.trim();

            // Validate and sanitize sort parameter
            List<String> allowedSortFields = Arrays.asList("id", "userId", "fullname", "gradeLevel", "email");
            if (sort == null || !allowedSortFields.contains(sort)) {
                sort = "fullname"; // default sort
            }

            // Validate pagination parameters
            page = (page == null || page < 0) ? 0 : page;
            limit = (limit == null || limit <= 0) ? 10 : limit;

            // Get enrolled student IDs for the classroom
            List<String> enrolledStudentIds = studentEnrollmentsRepository
                    .findByClassroomId(classroomId)
                    .stream()
                    .map(StudentEnrollmentsEntity::getStudentId)
                    .collect(Collectors.toList());

            // Get students not in classroom
            List<StudentEntity> allStudents;
            if (search.isEmpty()) {
                allStudents = studentRepository.findStudentsNotInClassroom(enrolledStudentIds);
            } else {
                allStudents = studentRepository.findStudentsNotInClassroom(enrolledStudentIds, search);
            }

            // Sort students
            Comparator<StudentEntity> comparator;
            switch (sort) {
                case "id":
                    comparator = Comparator.comparing(StudentEntity::getId);
                    break;
                case "userId":
                    comparator = Comparator.comparing(StudentEntity::getUserId);
                    break;
                case "gradeLevel":
                    comparator = Comparator.comparing(StudentEntity::getGradeLevel);
                    break;
                case "email":
                    comparator = Comparator.comparing(student -> student.getUser().getEmail());
                    break;
                default: // fullname
                    comparator = Comparator.comparing(student -> student.getUser().getFullname());
            }

            // Apply sorting direction
            if ("desc".equalsIgnoreCase(order)) {
                comparator = comparator.reversed();
            }

            // Sort the students
            allStudents.sort(comparator);

            // Paginate the results
            int start = page * limit;
            int end = Math.min((start + limit), allStudents.size());
            List<StudentEntity> paginatedStudents = allStudents.subList(start, end);

            // Convert to response DTOs
            List<StudentsResponse.StudentResponse> studentResponses = paginatedStudents.stream()
                    .map(StudentsResponse.StudentResponse::formStudentEntity)
                    .collect(Collectors.toList());

            // Prepare the response
            StudentsResponse response = new StudentsResponse();
            response.setStudents(studentResponses);
            response.setTotalElements((long) allStudents.size());
            response.setTotalPage((int) Math.ceil((double) allStudents.size() / limit));

            return response;
        } catch (Exception e) {
            log.error("Error in getStudents: ", e);
            throw new IllegalArgumentException("Error retrieving students: " + e.getMessage());
        }
    }

}
