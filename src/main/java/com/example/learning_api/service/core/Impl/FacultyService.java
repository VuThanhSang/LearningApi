package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.faculty.CreateFacultyRequest;
import com.example.learning_api.dto.request.faculty.ImportFacultyRequest;
import com.example.learning_api.dto.request.faculty.UpdateFacultyRequest;
import com.example.learning_api.dto.response.faculty.GetFacultiesResponse;
import com.example.learning_api.entity.sql.database.FacultyEntity;
import com.example.learning_api.entity.sql.database.MajorsEntity;
import com.example.learning_api.enums.ImportType;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.FacultyRepository;
import com.example.learning_api.repository.database.MajorsRepository;
import com.example.learning_api.service.common.ExcelReader;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IFacultyService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacultyService implements IFacultyService {
    private final FacultyRepository facultyRepository;
    private final ModelMapperService modelMapperService;
    private final MajorsRepository majorsRepository;
    private final ExcelReader excelReader;
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

    @Override
    public void importFaculty(ImportFacultyRequest body) {
        try{
            if (body.getFile()==null){
                throw new IllegalArgumentException(" File is required");
            }
            ObjectMapper mapper = new ObjectMapper();
            List<List<String>> data = excelReader.readExcel(body.getFile().getInputStream());
            for (int i = 1; i < data.size(); i++) {
                List<String> row = data.get(i);

                FacultyEntity facultyEntity = new FacultyEntity();
                facultyEntity.setName(row.get(0));
                facultyEntity.setDescription(row.get(1));
                facultyEntity.setDean(row.get(2));
                facultyEntity.setCreatedAt(new Date());
                facultyEntity.setUpdatedAt(new Date());
                facultyRepository.save(facultyEntity);
                List<String> list = mapper.readValue(row.get(3), new TypeReference<List<String>>(){});
                for (int j = 1; j < list.size(); j++) {
                    MajorsEntity majorsEntity = new MajorsEntity();
                    majorsEntity.setName(list.get(j));
                    majorsEntity.setFacultyId(facultyEntity.getId());
                    majorsRepository.save(majorsEntity);

                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
