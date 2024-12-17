package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.student.CreateStudentRequest;
import com.example.learning_api.dto.request.student.UpdateStudentRequest;
import com.example.learning_api.dto.response.cart.GetPaymentForStudent;
import com.example.learning_api.dto.response.cart.PaymentsResponse;
import com.example.learning_api.dto.response.student.CreateStudentResponse;
import com.example.learning_api.dto.response.student.GetStudentsResponse;
import com.example.learning_api.entity.sql.database.StudentEntity;

public interface IStudentService {
    CreateStudentResponse createStudent(CreateStudentRequest body);
    void updateStudent(UpdateStudentRequest body);
    void deleteStudent(String id);
    GetStudentsResponse getStudents(int page, int size, String search);
    StudentEntity getStudentByUserId(String userId);

    GetPaymentForStudent getPaymentForStudent(String userId, int page, int size, String sort, String order, String status, String search);
}
