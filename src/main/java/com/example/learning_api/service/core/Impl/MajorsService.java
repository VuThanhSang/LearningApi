package com.example.learning_api.service.core.Impl;

import com.example.learning_api.entity.sql.database.MajorsEntity;
import com.example.learning_api.repository.database.FacultyRepository;
import com.example.learning_api.repository.database.MajorsRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IMajorsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MajorsService implements IMajorsService {
    private final MajorsRepository majorsRepository;
    private final FacultyRepository facultyRepository;

    @Override
    public List<MajorsEntity> getAllMajors() {
        return majorsRepository.findAll();
    }

    @Override
    public MajorsEntity getMajorById(String id) {
        return majorsRepository.findById(id).orElse(null);
    }

    @Override
    public MajorsEntity createMajor(MajorsEntity majorsEntity) {
        if (majorsEntity.getName()==null){
            throw new IllegalArgumentException("Name is required");
        }
        if (majorsEntity.getFacultyId()==null){
            throw new IllegalArgumentException("FacultyId is required");
        }
        if (facultyRepository.findById(majorsEntity.getFacultyId()).orElse(null)==null){
            throw new IllegalArgumentException("FacultyId is not found");
        }

        return majorsRepository.save(majorsEntity);
    }

    @Override
    public MajorsEntity updateMajor(MajorsEntity majorsEntity) {
        return majorsRepository.save(majorsEntity);
    }

    @Override
    public void deleteMajor(String id) {
        majorsRepository.deleteById(id);
    }
}
