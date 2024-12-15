package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.progress.ProgressCompleteRequest;
import com.example.learning_api.entity.sql.database.LessonEntity;
import com.example.learning_api.entity.sql.database.ProgressEntity;
import com.example.learning_api.repository.database.LessonRepository;
import com.example.learning_api.repository.database.ProgressRepository;
import com.example.learning_api.service.core.IProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressService implements IProgressService {
    private final ProgressRepository progressRepository;
    private final LessonRepository lessonRepository;
    @Override
    public void markLessonAsCompleted(ProgressCompleteRequest body) {
        ProgressEntity progress = progressRepository.findByStudentIdAndClassroomIdAndLessonId(body.getStudentId(), body.getClassroomId(), body.getLessonId())
                .orElse(new ProgressEntity());

        progress.setStudentId(body.getStudentId());
        progress.setClassroomId(body.getClassroomId());
        progress.setLessonId(body.getLessonId());
        progress.setCompleted(true);
        progress.setCompletedAt(String.valueOf(System.currentTimeMillis()));

        progressRepository.save(progress);
        LessonEntity lesson = lessonRepository.findById(body.getLessonId()).orElse(null);
        if (lesson != null) {
            String sectionId = lesson.getSectionId();
            checkAndCompleteSection(body.getStudentId(), body.getClassroomId(), sectionId);
        }
    }
    private void checkAndCompleteSection(String studentId, String classroomId, String sectionId) {
        // Lấy tất cả các bài học thuộc section
        List<LessonEntity> lessons = lessonRepository.findBySectionIdOrderByIndex(sectionId);

        // Kiểm tra nếu tất cả các bài học đã được hoàn thành
        boolean allLessonsCompleted = lessons.stream().allMatch(lesson ->
                progressRepository.existsByStudentIdAndClassroomIdAndLessonIdAndCompleted(studentId, classroomId, lesson.getId(), true)
        );

        if (allLessonsCompleted) {
            // Đánh dấu section là hoàn thành
            ProgressEntity sectionProgress = progressRepository.findByStudentIdAndClassroomIdAndSectionId(studentId, classroomId, sectionId);
            if (sectionProgress == null) {
                sectionProgress = new ProgressEntity();
            }

            sectionProgress.setStudentId(studentId);
            sectionProgress.setClassroomId(classroomId);
            sectionProgress.setSectionId(sectionId);
            sectionProgress.setCompleted(true);
            sectionProgress.setCompletedAt(String.valueOf(System.currentTimeMillis()));

            progressRepository.save(sectionProgress);
        }
    }

    @Override
    public void markSectionAsCompleted(ProgressCompleteRequest body) {
        ProgressEntity progress = progressRepository.findByStudentIdAndClassroomIdAndSectionId(body.getStudentId(), body.getClassroomId(), body.getSectionId());
        if (progress == null) {
            progress = new ProgressEntity();
        }
        progress.setStudentId(body.getStudentId());
        progress.setClassroomId(body.getClassroomId());
        progress.setSectionId(body.getSectionId());
        progress.setCompleted(true);
        progress.setCompletedAt(String.valueOf(System.currentTimeMillis()));

        progressRepository.save(progress);
    }
}
