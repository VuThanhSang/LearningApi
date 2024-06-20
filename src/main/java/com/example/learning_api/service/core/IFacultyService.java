package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.faculty.CreateFacultyRequest;
import com.example.learning_api.dto.request.faculty.ImportFacultyRequest;
import com.example.learning_api.dto.request.faculty.UpdateFacultyRequest;
import com.example.learning_api.dto.response.faculty.GetFacultiesResponse;

public interface IFacultyService {
    void createFaculty(CreateFacultyRequest createFacultyRequest);
    void updateFaculty(UpdateFacultyRequest updateFacultyRequest);
    void deleteFaculty(String id);
    GetFacultiesResponse getFaculties( String search);
    void importFaculty(ImportFacultyRequest importFacultyRequest);
}
