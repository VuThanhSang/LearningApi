package com.example.learning_api.service.core;

import com.example.learning_api.entity.sql.database.AcademicYearsEntity;

import java.util.List;

public interface IAcademicYearsService{
    List<AcademicYearsEntity> getAllAcademicYears();
    AcademicYearsEntity getAcademicYearById(String id);
    AcademicYearsEntity createAcademicYear(AcademicYearsEntity academicYearsEntity);
    AcademicYearsEntity updateAcademicYear(AcademicYearsEntity academicYearsEntity);
    void deleteAcademicYear(String id);
}
