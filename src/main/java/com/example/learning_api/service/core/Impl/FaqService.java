package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.common.SourceUploadDto;
import com.example.learning_api.dto.request.faq.CreateFaqRequest;
import com.example.learning_api.dto.request.faq.UpdateFaqRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.entity.sql.database.FAQEntity;
import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.FaqStatus;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IFaqService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaqService implements IFaqService {
    private final ModelMapperService modelMapperService;
    private final FAQRepository faqRepository;
    private final StudentRepository studentRepository;
    private final ClassRoomRepository classRoomRepository;
    private final CloudinaryService cloudinaryService;
    @Override
    public void createFaq(CreateFaqRequest request) {
        validateRequest(request);
        FAQEntity faqEntity = createFaqEntity(request);
        faqEntity.setSources(new ArrayList<>());
        processSources(request.getSources(),request.getQuestion(), faqEntity);
        saveFaqEntity(faqEntity);
    }

    private void validateRequest(CreateFaqRequest request) {
        if (request.getQuestion() == null) {
            throw new IllegalArgumentException("Question is required");
        }
        if (request.getClassId() == null) {
            throw new IllegalArgumentException("ClassroomId is required");
        }
        if (classRoomRepository.findById(request.getClassId()).isEmpty()) {
            throw new IllegalArgumentException("ClassroomId is not found");
        }
        if (request.getStudentId() == null) {
            throw new IllegalArgumentException("UserId is required");
        }
        if (studentRepository.findById(request.getStudentId()).isEmpty()) {
            throw new IllegalArgumentException("UserId is not found");
        }
    }

    private FAQEntity createFaqEntity(CreateFaqRequest request) {
        FAQEntity faqEntity = modelMapperService.mapClass(request, FAQEntity.class);
        faqEntity.setStatus(request.getStatus() == null ? FaqStatus.DRAFT : FaqStatus.valueOf(request.getStatus()));
        faqEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        faqEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        return faqEntity;
    }

    public void processSources(List<SourceUploadDto> sources,String question, FAQEntity faqEntity) {
        if (sources.isEmpty()) {
            return;
        }

        for (SourceUploadDto source : sources) {
            try {
                FAQEntity.SourceDto sourceDto = processSource(source, question);
                faqEntity.getSources().add(sourceDto);
            } catch (IOException e) {
                log.error("Error processing source: " + e.getMessage());
                throw new IllegalArgumentException("Error processing source");
            }
        }
    }

    private FAQEntity.SourceDto processSource(SourceUploadDto source, String question) throws IOException {
        FAQEntity.SourceDto sourceDto = new FAQEntity.SourceDto();
        sourceDto.setType(source.getType());

        byte[] fileBytes = source.getPath().getBytes();
        String fileName = StringUtils.generateFileName(question, "Faq");

        CloudinaryUploadResponse response;
        switch (source.getType()) {
            case IMAGE:
                byte[] resizedImage = ImageUtils.resizeImage(fileBytes, 400, 400);
                response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName, resizedImage, "image");
                break;
            case VIDEO:
                String videoFileType = getFileExtension(source.getPath().getOriginalFilename());
                response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + videoFileType, fileBytes, "video");
                break;
            case DOCUMENT:
                String docFileType = getFileExtension(source.getPath().getOriginalFilename());
                response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + docFileType, fileBytes, "raw");
                break;
            default:
                throw new IllegalArgumentException("Unsupported source type");
        }

        sourceDto.setPath(response.getUrl() );
        return sourceDto;
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    private void saveFaqEntity(FAQEntity faqEntity) {
        try {
            faqRepository.save(faqEntity);
        } catch (Exception e) {
            log.error("Error saving FAQ entity: " + e.getMessage());
            throw new IllegalArgumentException("Error saving FAQ");
        }
    }

    @Override
    public void updateFaq(UpdateFaqRequest updateFaqRequest) {
        try {
            FAQEntity faqEntity = faqRepository.findById(updateFaqRequest.getId()).orElseThrow(() -> new IllegalArgumentException("Faq not found"));
            if (updateFaqRequest.getQuestion() != null)
                faqEntity.setQuestion(updateFaqRequest.getQuestion());
            if (updateFaqRequest.getStatus() != null)
                faqEntity.setStatus(FaqStatus.valueOf(updateFaqRequest.getStatus()));
            if (updateFaqRequest.getSources()!=null){
                faqEntity.getSources().clear();
                processSources(updateFaqRequest.getSources(),updateFaqRequest.getQuestion(), faqEntity);
            }
            faqEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            faqRepository.save(faqEntity);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteFaq(String id) {
        try {
            faqRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }
}
