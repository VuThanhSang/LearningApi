package com.example.learning_api.service.core.Impl;

import com.example.learning_api.entity.sql.database.MajorsEntity;
import com.example.learning_api.repository.database.MajorsRepository;
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
