package com.example.learning_api.dto.response.faculty;

import com.example.learning_api.entity.sql.database.FacultyEntity;
import lombok.Data;

import java.util.List;

@Data
public class GetFacultiesResponse {

    private List<FacultyEntity> faculties;

}
