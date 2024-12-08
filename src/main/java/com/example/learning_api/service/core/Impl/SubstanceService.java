package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.substance.CreateSubstanceRequest;
import com.example.learning_api.dto.request.substance.UpdateSubstanceRequest;
import com.example.learning_api.dto.response.lesson.GetSubstancesResponse;
import com.example.learning_api.entity.sql.database.SubstanceEntity;
import com.example.learning_api.enums.SubstanceStatus;
import com.example.learning_api.repository.database.LessonRepository;
import com.example.learning_api.repository.database.SubstanceRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ISubstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubstanceService implements ISubstanceService {
    private final LessonRepository lessonRepository;
    private final SubstanceRepository substanceRepository;
    private final ModelMapperService modelMapperService;

    @Override
    public void createSubstance(CreateSubstanceRequest body) {
        try{
            if (body.getLessonId()==null){
                throw new IllegalArgumentException("LessonId is required");
            }
            if (lessonRepository.findById(body.getLessonId()).isEmpty()){
                throw new IllegalArgumentException("LessonId is not found");
            }
            SubstanceEntity substanceEntity = modelMapperService.mapClass(body, SubstanceEntity.class);
            if (substanceEntity.getStatus()==null){
                substanceEntity.setStatus(SubstanceStatus.PRIVATE);
            }
            substanceEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            substanceEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            substanceRepository.save(substanceEntity);

        }
        catch (Exception e) {
            log.error("Error in createSubstance: ", e);
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public void deleteSubstance(String substanceId) {
        try{
            if (substanceRepository.findById(substanceId ).isEmpty()){
                throw new IllegalArgumentException("SubstanceId is not found");
            }
            substanceRepository.deleteById(substanceId);
        }
        catch (Exception e) {
            log.error("Error in deleteSubstance: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateSubstance(UpdateSubstanceRequest body) {
        try{
            SubstanceEntity substanceEntity= substanceRepository.findById(body.getId()).orElseThrow(()->new IllegalArgumentException("Id is not found"));
            if (body.getId()==null){
                throw new IllegalArgumentException("Id is required");
            }
            if (substanceEntity==null){
                throw new IllegalArgumentException("Id is not found");
            }
            if (body.getContent()!=null){
                substanceEntity.setContent(body.getContent());

            }
            if (body.getName()!=null){
                substanceEntity.setName(body.getName());
            }
            if (body.getStatus()!=null){
                substanceEntity.setStatus(SubstanceStatus.valueOf(body.getStatus()));
            }
            substanceEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            substanceRepository.save(substanceEntity);
        }
        catch (Exception e) {
            log.error("Error in updateSubstance: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public SubstanceEntity getSubstance(String substanceId) {
        try{
            return substanceRepository.findById(substanceId).orElseThrow(()->new IllegalArgumentException("Id is not found"));
        }
        catch (Exception e) {
            log.error("Error in getSubstance: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetSubstancesResponse getSubstancesByLessonId(String lessonId, Integer page, Integer size) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<SubstanceEntity> substanceEntities = substanceRepository.findByLessonId(lessonId, pageAble);
            GetSubstancesResponse getSubstancesResponse = new GetSubstancesResponse();
            List<GetSubstancesResponse.SubstanceResponse> substanceResponses = new ArrayList<>();
            for (SubstanceEntity substanceEntity : substanceEntities.getContent()) {
                GetSubstancesResponse.SubstanceResponse substanceResponse = modelMapperService.mapClass(substanceEntity, GetSubstancesResponse.SubstanceResponse.class);
                substanceResponses.add(substanceResponse);
            }
            getSubstancesResponse.setSubstances(substanceResponses);
            getSubstancesResponse.setTotalElements(substanceEntities.getTotalElements());
            getSubstancesResponse.setTotalPage(substanceEntities.getTotalPages());
            return getSubstancesResponse;
        }
        catch (Exception e) {
            log.error("Error in getSubstancesByLessonId: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
