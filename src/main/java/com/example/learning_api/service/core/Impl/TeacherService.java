package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.teacher.CreateTeacherRequest;
import com.example.learning_api.dto.request.teacher.UpdateTeacherRequest;
import com.example.learning_api.dto.response.teacher.CreateTeacherResponse;
import com.example.learning_api.dto.response.teacher.GetTeachersResponse;
import com.example.learning_api.entity.sql.database.SubjectSpecializationEntity;
import com.example.learning_api.entity.sql.database.TeacherEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.repository.database.MajorsRepository;
import com.example.learning_api.repository.database.SubjectSpecializationRepository;
import com.example.learning_api.repository.database.TeacherRepository;
import com.example.learning_api.repository.database.UserRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ITeacherService;
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
public class TeacherService implements ITeacherService {
    private final ModelMapperService modelMapperService;
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final SubjectSpecializationRepository subjectSpecializationRepository;
    private final MajorsRepository majorsRepository;
    @Override
    public CreateTeacherResponse createTeacher(CreateTeacherRequest body) {
        try{
            UserEntity userEntity = userRepository.findById(body.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            if (body.getUserId()==null){
                throw new IllegalArgumentException("UserId is required");
            }
            if (userEntity==null){
                throw new IllegalArgumentException("UserId is not found");
            }
            CreateTeacherResponse resData = new CreateTeacherResponse();
            TeacherEntity teacherEntity = modelMapperService.mapClass(body, TeacherEntity.class);
            teacherEntity.setUser(userEntity);
            teacherRepository.save(teacherEntity);
            resData.setUser(userRepository.findById(body.getUserId()).get());
            resData.setUserId(body.getUserId());
            resData.setBio(body.getBio());
            resData.setQualifications(body.getQualifications());
            return resData;

        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateTeacher(UpdateTeacherRequest body) {
        try{
            TeacherEntity teacherEntity = teacherRepository.findById(body.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
            if (body.getBio()!=null){
                teacherEntity.setBio(body.getBio());
            }
            if (body.getQualifications()!=null){
                teacherEntity.setQualifications(body.getQualifications());
            }
            teacherRepository.save(teacherEntity);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteTeacher(String id) {
        try{
            teacherRepository.deleteById(id);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetTeachersResponse getTeachers(int page, int size, String search) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<TeacherEntity> teacherEntities = teacherRepository.findByNameContaining(search, pageAble);
            GetTeachersResponse resData = new GetTeachersResponse();
            List<GetTeachersResponse.TeacherResponse> teacherResponses = new ArrayList<>();
            for (TeacherEntity teacherEntity : teacherEntities){
                GetTeachersResponse.TeacherResponse teacherResponse = modelMapperService.mapClass(teacherEntity, GetTeachersResponse.TeacherResponse.class);
                teacherResponses.add(teacherResponse);
            }
            resData.setTeachers(teacherResponses);
            resData.setTotalElements(teacherEntities.getTotalElements());
            resData.setTotalPage(teacherEntities.getTotalPages());
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void addSubjectSpecialization(String teacherId, String majorId) {
        try{
            TeacherEntity teacherEntity = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
            if (teacherEntity==null){
                throw new IllegalArgumentException("Teacher not found");
            }
            if (majorId==null){
                throw new IllegalArgumentException("MajorId is required");
            }
            if (majorsRepository.findById(majorId)==null){
                throw new IllegalArgumentException("MajorId is not found");
            }
            SubjectSpecializationEntity subjectSpecializationEntity = new SubjectSpecializationEntity();
            subjectSpecializationEntity.setTeacherId(teacherId);
            subjectSpecializationEntity.setMajorsId(majorId);
            subjectSpecializationRepository.save(subjectSpecializationEntity);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public TeacherEntity getTeacherByUserId(String teacherId) {
        try{
            return teacherRepository.findByUserId(teacherId);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
