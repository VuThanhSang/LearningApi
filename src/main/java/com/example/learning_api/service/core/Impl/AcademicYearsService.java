package com.example.learning_api.service.core.Impl;

import com.example.learning_api.entity.sql.database.AcademicYearsEntity;
import com.example.learning_api.repository.database.AcademicYearsRepository;
import com.example.learning_api.service.core.IAcademicYearsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class AcademicYearsService implements IAcademicYearsService {

    private final AcademicYearsRepository academicYearsRepository;

    @Override
    public List<AcademicYearsEntity> getAllAcademicYears() {
        return academicYearsRepository.findAll();
    }

    @Override
    public AcademicYearsEntity getAcademicYearById(String id) {
        return academicYearsRepository.findById(id).orElse(null);
    }

    @Override
    public AcademicYearsEntity createAcademicYear(AcademicYearsEntity academicYearsEntity) {
        return academicYearsRepository.save(academicYearsEntity);

    }

    @Override
    public AcademicYearsEntity updateAcademicYear(AcademicYearsEntity academicYearsEntity) {
        return academicYearsRepository.save(academicYearsEntity);
    }

    @Override
    public void deleteAcademicYear(String id) {
        academicYearsRepository.deleteById(id);

    }
}
