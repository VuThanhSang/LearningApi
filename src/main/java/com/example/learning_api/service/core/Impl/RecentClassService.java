package com.example.learning_api.service.core.Impl;

import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.entity.sql.database.RecentClassEntity;
import com.example.learning_api.entity.sql.database.StudentEnrollmentsEntity;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.core.IRecentClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecentClassService implements IRecentClassService {
    private final RecentClassRepository recentClassRepository;
    private final ClassRoomRepository classRoomRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentEnrollmentsRepository studentEnrollmentsService;
    @Override
    public void createRecentClass(String userId , String role,String classroomId) {
        try{

           if (classroomId== null) {
                throw new IllegalArgumentException("ClassroomId is required");
            }
            if (classRoomRepository.findById(classroomId).isEmpty()) {
                throw new IllegalArgumentException("Classroom not found");
            }
            if(role.equals("USER")){
                RecentClassEntity recentClassEntity = recentClassRepository.findByStudentIdAndClassroomId(userId, classroomId);
                if (recentClassEntity == null)
                {
                    RecentClassEntity newData = new RecentClassEntity();
                    newData.setStudentId(userId);
                    newData.setClassroomId(classroomId);
                    newData.setLastAccessedAt(String.valueOf(System.currentTimeMillis()));
                    StudentEnrollmentsEntity studentEnrollmentsEntity = studentEnrollmentsService.findByStudentIdAndClassroomId(userId,classroomId);
                    if (studentEnrollmentsEntity == null)
                    {
                        return;
                    }else{
                        recentClassRepository.save(newData);

                    }
                }
                else{
                    recentClassEntity.setLastAccessedAt(String.valueOf(System.currentTimeMillis()));
                    recentClassRepository.save(recentClassEntity);
                }
            }
            else if(role.equals("TEACHER")){
                RecentClassEntity recentClassEntity = recentClassRepository.findByTeacherIdAndClassroomId(userId,classroomId);
                ClassRoomEntity classRoomEntity = classRoomRepository.findById(classroomId).get();
                if (!Objects.equals(classRoomEntity.getTeacherId(), userId)) {
                   return;
                }
                if (recentClassEntity == null)
                {
                    RecentClassEntity newData = new RecentClassEntity();
                    newData.setLastAccessedAt(String.valueOf(System.currentTimeMillis()));
                    newData.setTeacherId(userId);
                    newData.setClassroomId(classroomId);

                    recentClassRepository.save(newData);
                }
                else{
                    recentClassEntity.setLastAccessedAt(String.valueOf(System.currentTimeMillis()));
                    recentClassRepository.save(recentClassEntity);
                }
            }
            else{
                throw new IllegalArgumentException("StudentId or TeacherId is required");
            }

        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }



    }

    @Override
    public void updateRecentClass(String studentId, String classroomId, String lastAccessedAt) {
        RecentClassEntity recentClassEntity = recentClassRepository.findByStudentIdAndClassroomId(studentId, classroomId);
        if (recentClassEntity == null) {
            throw new IllegalArgumentException("Recent class not found");
        }
        recentClassEntity.setLastAccessedAt(lastAccessedAt);
        recentClassRepository.save(recentClassEntity);
    }


    @Override
    public void deleteRecentClass(String id) {
        if (recentClassRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Recent class not found");
        }
        recentClassRepository.deleteById(id);

    }
}
