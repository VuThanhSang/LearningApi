package com.example.learning_api.service.core.Impl;

import com.example.learning_api.entity.sql.database.RecentClassEntity;
import com.example.learning_api.repository.database.ClassRoomRepository;
import com.example.learning_api.repository.database.RecentClassRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.service.core.IRecentClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecentClassService implements IRecentClassService {
    private final RecentClassRepository recentClassRepository;
    private final ClassRoomRepository classRoomRepository;
    private final StudentRepository studentRepository;

    @Override
    public void createRecentClass(RecentClassEntity body) {
        if (body.getClassroomId() == null) {
            throw new IllegalArgumentException("ClassroomId is required");
        }
        if (body.getStudentId() == null) {
            throw new IllegalArgumentException("StudentId is required");
        }
        if (classRoomRepository.findById(body.getClassroomId()).isEmpty()) {
            throw new IllegalArgumentException("Classroom not found");
        }
        if (studentRepository.findById(body.getStudentId()).isEmpty()) {
            throw new IllegalArgumentException("Student not found");
        }
        recentClassRepository.save(body);


    }

    @Override
    public void updateRecentClass(String studentId, String classroomId, Date lastAccessedAt) {
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
