package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.classroom.CreateClassRoomRequest;
import com.example.learning_api.dto.request.classroom.DeleteClassRoomRequest;
import com.example.learning_api.dto.request.classroom.UpdateClassRoomRequest;
import com.example.learning_api.dto.response.ClassRoomResponse.CreateClassRoomResponse;
import com.example.learning_api.dto.response.ClassRoomResponse.GetClassRoomsResponse;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.entity.sql.database.ClassRoomEntity;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.ClassRoomRepository;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IClassRoomService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassRoomService implements IClassRoomService {
    private final ModelMapperService modelMapperService;
    private final ClassRoomRepository classRoomRepository;
    private final CloudinaryService cloudinaryService;
    @Override
    public CreateClassRoomResponse createClassRoom(CreateClassRoomRequest body) {
        try{
            if (!ImageUtils.isValidImageFile(body.getImage())) {
                throw new CustomException(ErrorConstant.IMAGE_INVALID);
            }
            if (body.getName()==null){
                throw new IllegalArgumentException("Name is required");
            }

            ClassRoomEntity classRoomEntity = modelMapperService.mapClass(body, ClassRoomEntity.class);
            CreateClassRoomResponse resData = new CreateClassRoomResponse();
            if (body.getImage()!=null){
                byte[] originalImage = new byte[0];
                originalImage = body.getImage().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "classroom"),
                        newImage
                );
                classRoomEntity.setImage(imageUploaded.getUrl());
            }

            classRoomRepository.save(classRoomEntity);

            resData.setName(classRoomEntity.getName());
            resData.setDescription(classRoomEntity.getDescription());
            resData.setImage(classRoomEntity.getImage());
            return resData;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateClassRoom(UpdateClassRoomRequest body) {
        try {
            ClassRoomEntity classroom = classRoomRepository.findById(body.getId())
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            if(body.getImage()!=null){
                byte[] originalImage = new byte[0];
                originalImage = body.getImage().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "classroom"),
                        newImage
                );
                classroom.setImage(imageUploaded.getUrl());
            }
            if(body.getName()!=null){
                classroom.setName(body.getName());
            }
            if(body.getDescription()!=null){
                classroom.setDescription(body.getDescription());
            }
            classRoomRepository.save(classroom);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }


    @Override
    public void deleteClassRoom(String classroomId) {
        try {
             ClassRoomEntity classroom = classRoomRepository.findById(classroomId)
                    .orElseThrow(() -> new CustomException(ErrorConstant.NOT_FOUND));
            classRoomRepository.delete(classroom);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetClassRoomsResponse getClassRooms(int page, int size, String search) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<ClassRoomEntity> classRooms = classRoomRepository.findByNameContaining(search, pageAble);
            List<GetClassRoomsResponse.ClassRoomResponse> resData = new ArrayList<>();
            for (ClassRoomEntity classRoom : classRooms){
                log.info("classRoom: {}", classRoom);
                GetClassRoomsResponse.ClassRoomResponse classRoomResponse = modelMapperService.mapClass(classRoom, GetClassRoomsResponse.ClassRoomResponse.class);
                resData.add(classRoomResponse);
            }
            GetClassRoomsResponse res = new GetClassRoomsResponse();
            res.setClassRooms(resData);
            res.setTotalPage(classRooms.getTotalPages());
            res.setTotalElements(classRooms.getTotalElements());
            return res;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }
}
