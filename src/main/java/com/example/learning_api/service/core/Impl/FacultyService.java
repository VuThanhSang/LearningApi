package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.faculty.CreateFacultyRequest;
import com.example.learning_api.dto.request.faculty.UpdateFacultyRequest;
import com.example.learning_api.dto.response.faculty.GetFacultiesResponse;
import com.example.learning_api.entity.sql.database.FacultyEntity;
import com.example.learning_api.repository.database.FacultyRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IFacultyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacultyService implements IFacultyService {
    private final FacultyRepository facultyRepository;
    private final ModelMapperService modelMapperService;

    @Override
    public void createFaculty(CreateFacultyRequest createFacultyRequest) {
        try{
            if (createFacultyRequest.getName()==null){
                throw new IllegalArgumentException("Name is required");
            }
            if (createFacultyRequest.getDescription()==null){
                throw new IllegalArgumentException("Description is required");
            }
            FacultyEntity facultyEntity = modelMapperService.mapClass(createFacultyRequest, FacultyEntity.class);
            facultyEntity.setCreatedAt(new Date());
            facultyEntity.setUpdatedAt(new Date());
            facultyRepository.save(facultyEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void updateFaculty(UpdateFacultyRequest updateFacultyRequest) {
        try{
            FacultyEntity facultyEntity = facultyRepository.findById(updateFacultyRequest.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (updateFacultyRequest.getId()==null){
                throw new IllegalArgumentException("Id is required");
            }
            if (facultyEntity==null){
                throw new IllegalArgumentException("Id is not found");
            }
            if (updateFacultyRequest.getName()!=null){
                facultyEntity.setName(updateFacultyRequest.getName());
            }
            if (updateFacultyRequest.getDescription()!=null){
                facultyEntity.setDescription(updateFacultyRequest.getDescription());
            }
            facultyEntity.setUpdatedAt(new Date());
            facultyRepository.save(facultyEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteFaculty(String id) {
        try{
            FacultyEntity facultyEntity = facultyRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            facultyRepository.delete(facultyEntity);
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetFacultiesResponse getFaculties(String search) {
        try{
            List<FacultyEntity> facultyEntities = facultyRepository.findByNameContaining(search);
            GetFacultiesResponse getFacultiesResponse = new GetFacultiesResponse();

            getFacultiesResponse.setFaculties(facultyEntities);
            return getFacultiesResponse;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
