package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.student.CreateStudentRequest;
import com.example.learning_api.dto.request.student.UpdateStudentRequest;
import com.example.learning_api.dto.response.cart.GetPaymentForStudent;
import com.example.learning_api.dto.response.student.CreateStudentResponse;
import com.example.learning_api.dto.response.student.GetStudentsResponse;
import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TransactionEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.repository.database.ClassRoomRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TransactionRepository;
import com.example.learning_api.repository.database.UserRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IStudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService implements IStudentService {

    private final ModelMapperService modelMapperService;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public CreateStudentResponse createStudent(CreateStudentRequest body) {
        try{
            UserEntity userEntity = userRepository.findById(body.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            if (body.getUserId()==null){
                throw new IllegalArgumentException("UserId is required");
            }
            if (userEntity==null){
                throw new IllegalArgumentException("UserId is not found");
            }
            StudentEntity studentEntity = modelMapperService.mapClass(body, StudentEntity.class);
            studentEntity.setUser(userEntity);
            CreateStudentResponse resData = new CreateStudentResponse();
            studentRepository.save(studentEntity);
            resData.setUser(userRepository.findById(body.getUserId()).get());
            resData.setUserId(body.getUserId());
            resData.setGradeLevel(body.getGradeLevel());
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateStudent(UpdateStudentRequest body) {
        try{
            StudentEntity studentEntity = studentRepository
            .findById(body.getId())
            .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            if (body.getGradeLevel()!=null){
                studentEntity.setGradeLevel(body.getGradeLevel());
            }
            if (body.getAddress()!=null){
                studentEntity.setAddress(body.getAddress());
            }
            if (body.getPhone()!=null){
                studentEntity.setPhone(body.getPhone());
            }
            if (body.getGender()!=null){
                studentEntity.setGender(body.getGender());
            }

            if (body.getDateOfBirth()!=null){
                studentEntity.setDateOfBirth(body.getDateOfBirth());
            }

            studentRepository.save(studentEntity);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void deleteStudent(String id) {
        try{
            studentRepository.deleteById(id);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetStudentsResponse getStudents(int page, int size, String search) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<StudentEntity> studentEntities = studentRepository.findByNameContaining(search, pageAble);
            List<GetStudentsResponse.StudentResponse> studentResponses = new ArrayList<>();
            for (StudentEntity studentEntity : studentEntities){
                GetStudentsResponse.StudentResponse studentResponse = modelMapperService.mapClass(studentEntity, GetStudentsResponse.StudentResponse.class);

                studentResponses.add(studentResponse);
            }
            GetStudentsResponse resData = new GetStudentsResponse();
            resData.setStudents(studentResponses);
            resData.setTotalElements(studentEntities.getTotalElements());
            resData.setTotalPage(studentEntities.getTotalPages());
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public StudentEntity getStudentByUserId(String userId) {
        try{
            return studentRepository.findByUserId(userId);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetPaymentForStudent getPaymentForStudent(String userId, int page, int size) {
        try {
            StudentEntity studentEntity = studentRepository.findByUserId(userId);
            if (studentEntity == null) {
                throw new IllegalArgumentException("Student not found");
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionEntity> transactionEntities = transactionRepository.findByUserId(userId, pageable);
            List<GetPaymentForStudent.Transaction> transactions = new ArrayList<>();
            for (TransactionEntity transactionEntity : transactionEntities) {
                GetPaymentForStudent.Transaction transaction = modelMapperService.mapClass(transactionEntity, GetPaymentForStudent.Transaction.class);
                transaction.setClassroom(classRoomRepository.findById(transactionEntity.getClassroomId()).get());
                transactions.add(transaction);
            }
            GetPaymentForStudent resData = new GetPaymentForStudent();
            resData.setTotalPrice(transactionEntities.stream().mapToLong(TransactionEntity::getAmount).sum());
            resData.setTotalClassroom(transactionEntities.stream().map(TransactionEntity::getClassroomId).distinct().count());
            resData.setTotalElement(transactionEntities.getTotalElements());
            resData.setTotalPage(transactionEntities.getTotalPages());
            resData.setTransactions(transactions);
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
