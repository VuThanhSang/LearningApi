package com.example.learning_api.service.core;

import com.example.learning_api.entity.sql.database.MajorsEntity;

import java.util.List;

public interface IMajorsService {
    List<MajorsEntity> getAllMajors();
    MajorsEntity getMajorById(String id);
    MajorsEntity createMajor(MajorsEntity majorsEntity);
    MajorsEntity updateMajor(MajorsEntity majorsEntity);
    void deleteMajor(String id);
}
