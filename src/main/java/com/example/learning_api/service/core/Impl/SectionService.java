package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.section.CreateSectionRequest;
import com.example.learning_api.dto.request.section.DeleteSectionRequest;
import com.example.learning_api.dto.request.section.UpdateSectionRequest;
import com.example.learning_api.dto.response.section.CreateSectionResponse;
import com.example.learning_api.dto.response.section.GetSectionsResponse;
import com.example.learning_api.entity.sql.database.LessonEntity;
import com.example.learning_api.entity.sql.database.SectionEntity;
import com.example.learning_api.enums.SectionStatus;
import com.example.learning_api.repository.database.ClassRoomRepository;
import com.example.learning_api.repository.database.LessonRepository;
import com.example.learning_api.repository.database.SectionRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ISectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionService implements ISectionService {
    private final ModelMapperService modelMapperService;
    private final SectionRepository sectionRepository;
    private final ClassRoomRepository classRoomRepository;
    private final LessonRepository lessonRepository;
    @Override
    public CreateSectionResponse createSection(CreateSectionRequest body) {
        try {
            if (body.getName()==null){
                throw new IllegalArgumentException("Name is required");
            }
            if (body.getClassRoomId()==null){
                throw new IllegalArgumentException("ClassRoomId is required");
            }
            if (classRoomRepository.findById(body.getClassRoomId()).isEmpty()){
                throw new IllegalArgumentException("ClassRoomId is not found");
            }

            SectionEntity sectionEntity = modelMapperService.mapClass(body, SectionEntity.class);
            CreateSectionResponse resData = new CreateSectionResponse();
            Integer index = sectionRepository.findMaxIndexByClassRoomId(body.getClassRoomId());
            sectionEntity.setIndex(index==null?0:index+1);
            sectionEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            sectionEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            sectionRepository.save(sectionEntity);
            resData.setName(sectionEntity.getName());
            resData.setDescription(sectionEntity.getDescription());
            resData.setClassRoomId(sectionEntity.getClassRoomId());
            resData.setId(sectionEntity.getId());
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateSection(UpdateSectionRequest body) {
        try{
            SectionEntity sectionEntity = sectionRepository.findById(body.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Section not found"));
            if (body.getName()!=null){
                sectionEntity.setName(body.getName());
            }
            if (body.getDescription()!=null){

                sectionEntity.setDescription(body.getDescription());
            }
            if (body.getIndex()!=null){
                sectionEntity.setIndex(body.getIndex());
            }
            if (body.getStatus()!=null){
                sectionEntity.setStatus(SectionStatus.valueOf(body.getStatus()));
            }
            sectionEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            sectionRepository.save(sectionEntity);

        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteSection(DeleteSectionRequest id) {
        try{
            sectionRepository.deleteById(id.getId());
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetSectionsResponse getSections(int page, int size, String search, String role) {
        List<String> statuses = new ArrayList<>();
        if (role.equals("TEACHER")){
            statuses.add(SectionStatus.PUBLIC.toString());
            statuses.add(SectionStatus.PRIVATE.toString());
        }
        else {
            statuses.add(SectionStatus.PUBLIC.toString());
        }
        Pageable pageAble = PageRequest.of(page, size);
        Page<SectionEntity> sectionEntities = sectionRepository.findByClassRoomId(search, pageAble,statuses);
        List<GetSectionsResponse.SectionResponse> sectionResponses = modelMapperService.mapList(sectionEntities.getContent(), GetSectionsResponse.SectionResponse.class);
        GetSectionsResponse resData = new GetSectionsResponse();
        resData.setTotalPage(sectionEntities.getTotalPages());
        resData.setTotalElements(sectionEntities.getTotalElements());
        resData.setSections(sectionResponses);
        return resData;
    }

    @Override
    public GetSectionsResponse getSectionsByClassRoomId(String classRoomId, int page, int size, String role) {
        List<String> statuses = new ArrayList<>();
        if (role.equals("TEACHER")){
            statuses.add(SectionStatus.PUBLIC.toString());
            statuses.add(SectionStatus.PRIVATE.toString());
        }
        else {
            statuses.add(SectionStatus.PUBLIC.toString());
        }
        Pageable pageAble = PageRequest.of(page, size);
        Page<SectionEntity> sectionEntities = sectionRepository.findByClassRoomId(classRoomId, pageAble,statuses);
        List<GetSectionsResponse.SectionResponse> sectionResponses = modelMapperService.mapList(sectionEntities.getContent(), GetSectionsResponse.SectionResponse.class);
        for (GetSectionsResponse.SectionResponse sectionResponse : sectionResponses){
            List<LessonEntity> lessonEntities = lessonRepository.findBySectionId(sectionResponse.getId(), Sort.by(Sort.Direction.ASC, "index"),statuses);
            sectionResponse.setLessons(lessonEntities);
        }
        GetSectionsResponse resData = new GetSectionsResponse();
        resData.setTotalPage(sectionEntities.getTotalPages());
        resData.setTotalElements(sectionEntities.getTotalElements());
        resData.setSections(sectionResponses);
        return resData;
    }
}
