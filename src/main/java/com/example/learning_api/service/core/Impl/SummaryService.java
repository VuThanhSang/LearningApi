package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.summary.CreateSummaryRequest;
import com.example.learning_api.dto.request.summary.UpdateSummaryRequest;
import com.example.learning_api.dto.response.summary.GetSummaryResponse;
import com.example.learning_api.entity.sql.database.CourseEntity;
import com.example.learning_api.entity.sql.database.SummaryEntity;
import com.example.learning_api.repository.database.CourseRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.SummaryRepository;
import com.example.learning_api.repository.database.TermsRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ISummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SummaryService implements ISummaryService {

    private final SummaryRepository summaryRepository;
    private final ModelMapperService modelMapperService;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final TermsRepository termRepository;
    @Override
    public void createSummary(CreateSummaryRequest body) {
        try {
            if (body.getStudentId() == null) {
                throw new IllegalArgumentException("StudentId is required");
            }
            if (studentRepository.findById(body.getStudentId()).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            if (body.getTermId() == null) {
                throw new IllegalArgumentException("TermId is required");
            }
            if (termRepository.findById(body.getTermId()).isEmpty()) {
                throw new IllegalArgumentException("TermId is not found");
            }
            if (body.getCourseId() == null) {
                throw new IllegalArgumentException("CourseId is required");
            }
            if (courseRepository.findById(body.getCourseId()).isEmpty()) {
                throw new IllegalArgumentException("CourseId is not found");
            }
            if (summaryRepository.findByStudentIdAndTermIdAndCourseId(body.getStudentId(), body.getTermId(), body.getCourseId())!=null) {
                throw new IllegalArgumentException("Summary already exists");
            }
            SummaryEntity summaryEntity = modelMapperService.mapClass(body, SummaryEntity.class);
            if (body.getIsPassed()==1){
                summaryEntity.setPassed(true);
            }
            else {
                summaryEntity.setPassed(false);
            }
            summaryRepository.save(summaryEntity);

        } catch (Exception e) {
            log.error("Error in createSummary: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateSummary(UpdateSummaryRequest body) {
        try {
            if (body.getId() == null) {
                throw new IllegalArgumentException("Id is required");
            }
            SummaryEntity summary = summaryRepository.findById(body.getId()).orElse(null);
            if (summary == null) {
                throw new IllegalArgumentException("Summary is not found");
            }
            if (body.getFinalExamGrade()!=0)
                summary.setFinalExamGrade(body.getFinalExamGrade());
            if (body.getFinalGrade()!=0)
                summary.setFinalGrade(body.getFinalGrade());
            if (body.getMidTermGrade()!=0)
                summary.setMidTermGrade(body.getMidTermGrade());
            if (body.isPassed())
                summary.setPassed(body.isPassed());

            summaryRepository.save(summary);

        } catch (Exception e) {
            log.error("Error in updateSummary: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }


    }

    @Override
    public void deleteSummary(String id) {
        try {
            if (summaryRepository.findById(id).isEmpty()) {
                throw new IllegalArgumentException("SummaryId is not found");
            }
            summaryRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error in deleteSummary: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public SummaryEntity getSummary(String id) {
        try {
            return summaryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("SummaryId is not found"));
        } catch (Exception e) {
            log.error("Error in getSummary: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public  List<GetSummaryResponse>  getSummariesByStudentId(String studentId) {
        try {
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            List<SummaryEntity> data= summaryRepository.findByStudentId(studentId);
            List<GetSummaryResponse> response = new ArrayList<>();
            for (SummaryEntity summaryEntity : data) {
                GetSummaryResponse resData = new GetSummaryResponse();
                CourseEntity courseEntity = courseRepository.findById(summaryEntity.getCourseId()).orElse(null);
                resData.setId(summaryEntity.getId());
                resData.setCourseId(courseEntity.getId());
                resData.setCourseName(courseEntity.getName());
                resData.setFinalExamGrade(summaryEntity.getFinalExamGrade());
                resData.setFinalGrade(summaryEntity.getFinalGrade());
                resData.setMidTermGrade(summaryEntity.getMidTermGrade());
                resData.setPassed(summaryEntity.isPassed());
                response.add(resData);
            }
            return response;
        } catch (Exception e) {
            log.error("Error in getSummariesByStudentId: ", e);
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public List<GetSummaryResponse> getSummariesByStudentIdAndTermId(String studentId, String termId) {
        try {
            if (studentRepository.findById(studentId).isEmpty()) {
                throw new IllegalArgumentException("StudentId is not found");
            }
            if (termRepository.findById(termId).isEmpty()) {
                throw new IllegalArgumentException("TermId is not found");
            }
           List<SummaryEntity> data = summaryRepository.findByStudentIdAndTermId(studentId, termId);
            List<GetSummaryResponse> response = new ArrayList<>();
            for (SummaryEntity summaryEntity : data) {
                GetSummaryResponse resData = new GetSummaryResponse();
                CourseEntity courseEntity = courseRepository.findById(summaryEntity.getCourseId()).orElse(null);
                resData.setId(summaryEntity.getId());
                resData.setCourseId(courseEntity.getId());
                resData.setCourseName(courseEntity.getName());
                resData.setFinalExamGrade(summaryEntity.getFinalExamGrade());
                resData.setFinalGrade(summaryEntity.getFinalGrade());
                resData.setMidTermGrade(summaryEntity.getMidTermGrade());
                resData.setPassed(summaryEntity.isPassed());
                response.add(resData);
            }
            return response;
        } catch (Exception e) {
            log.error("Error in getSummariesByStudentIdAndTermId: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }

    }
}