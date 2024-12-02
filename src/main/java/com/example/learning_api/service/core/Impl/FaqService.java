package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.dto.common.SourceUploadDto;
import com.example.learning_api.dto.request.faq.CreateFaqRequest;
import com.example.learning_api.dto.request.faq.UpdateFaqRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.faq.GetFaqDetailResponse;
import com.example.learning_api.dto.response.faq.GetFaqsResponse;
import com.example.learning_api.entity.sql.database.DeadlineEntity;
import com.example.learning_api.entity.sql.database.FAQEntity;
import com.example.learning_api.entity.sql.database.FaqCommentEntity;
import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.enums.FaqSourceType;
import com.example.learning_api.enums.FaqStatus;
import com.example.learning_api.enums.FileOwnerType;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IFaqService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaqService implements IFaqService {
    private final ModelMapperService modelMapperService;
    private final FAQRepository faqRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;
    private final CloudinaryService cloudinaryService;
    private final FileRepository fileRepository;
    private final FaqCommentRepository faqCommentRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    public void processFiles (List<MultipartFile> files,String title, FAQEntity deadlineEntity){
        if (files == null) {
            return;
        }
        for (MultipartFile file : files) {
            try {
                FAQEntity.SourceDto fileDto = processFile(file, title);
                FileEntity fileEntity = new FileEntity();
                fileEntity.setUrl(fileDto.getPath());
                fileEntity.setType(fileDto.getType().name());
                fileEntity.setOwnerType(FileOwnerType.FAQ);
                fileEntity.setOwnerId(deadlineEntity.getId());
                fileEntity.setExtension(fileDto.getPath().substring(fileDto.getPath().lastIndexOf(".") + 1));
                fileEntity.setName(file.getOriginalFilename());
                fileEntity.setSize(String.valueOf(file.getSize()));
                fileEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                fileEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                fileRepository.save(fileEntity);
            } catch (IOException e) {
                log.error("Error processing file: ", e);
                throw new IllegalArgumentException("Error processing file: " + e.getMessage());
            }
        }
    }

    public FAQEntity.SourceDto processFile(MultipartFile file, String title) throws IOException {
        byte[] fileBytes = file.getBytes();
        String fileName = StringUtils.generateFileName(file.getOriginalFilename(), "deadline");
        CloudinaryUploadResponse response;

        String contentType = file.getContentType();
        if (contentType.startsWith("image/")) {
            byte[] resizedImage = ImageUtils.resizeImage(fileBytes, 400, 400);
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName, resizedImage, "image");
        } else if (contentType.startsWith("video/")) {
            String videoFileType = getFileExtension(file.getOriginalFilename());
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + videoFileType, fileBytes, "video");
        } else if (contentType.startsWith("application/")) {
            String docFileType = getFileExtension(file.getOriginalFilename());
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + docFileType, fileBytes, "raw");
        }  else if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName + ".docx", fileBytes, "raw");
        }
        else {
            throw new IllegalArgumentException("Unsupported source type");
        }

        return FAQEntity.SourceDto.builder()
                .path(response.getSecureUrl())
                .type(contentType.startsWith("image/") ? FaqSourceType.IMAGE : contentType.startsWith("video/") ? FaqSourceType.VIDEO : FaqSourceType.DOCUMENT)
                .build();
    }
    @Override
    public void createFaq(CreateFaqRequest request) {
        validateRequest(request);
        FAQEntity faqEntity = new FAQEntity();
        faqEntity.setQuestion(request.getQuestion());
        faqEntity.setUserId(request.getUserId());
        faqEntity.setStatus(FaqStatus.PENDING);
        faqEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        faqEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
//        faqEntity.setSources(new ArrayList<>());
        faqRepository.save(faqEntity);
        processFiles(request.getSources(),request.getQuestion(), faqEntity);
    }

    private void validateRequest(CreateFaqRequest request) {
        if (request.getQuestion() == null) {
            throw new IllegalArgumentException("Question is required");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("UserId is required");
        }
        if (userRepository.findById(request.getUserId()).isEmpty()) {
            throw new IllegalArgumentException("UserId is not found");
        }
    }

    private FAQEntity createFaqEntity(CreateFaqRequest request) {
        FAQEntity faqEntity = modelMapperService.mapClass(request, FAQEntity.class);
        faqEntity.setStatus(FaqStatus.PENDING);
        faqEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        faqEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        return faqEntity;
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
//                faqEntity.getSources().clear();
                processFiles(updateFaqRequest.getSources(),updateFaqRequest.getQuestion(), faqEntity);
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

    @Override
    public GetFaqDetailResponse getFaqDetail(String id) {
        try{
            FAQEntity faqEntity = faqRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Faq not found"));
            GetFaqDetailResponse response = modelMapperService.mapClass(faqEntity, GetFaqDetailResponse.class);
            response.setSources(fileRepository.findByOwnerIdAndOwnerType(id, FileOwnerType.FAQ.name()));
            List<FaqCommentEntity> comments = faqCommentRepository.findByFaqId(id);
            for (FaqCommentEntity comment : comments) {
                comment.setSources(fileRepository.findByOwnerIdAndOwnerType(comment.getId(), FileOwnerType.FAQ_COMMENT.name()));
            }
            response.setComments(comments);
            return response;
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetFaqsResponse getFaqs(Integer page, Integer size, String search, String sort, String order) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), sort));
            Page<FAQEntity> faqPage;

            if (search == null || search.isEmpty()) {
                faqPage = faqRepository.findAll(pageable);
            } else {
                faqPage = faqRepository.findByQuestionContainingIgnoreCase(search, pageable);
            }

            List<GetFaqsResponse.Faq> faqs = faqPage.getContent().stream().map(faqEntity -> {
                GetFaqsResponse.Faq faq = new GetFaqsResponse.Faq();
                faq.setId(faqEntity.getId());
                faq.setQuestion(faqEntity.getQuestion());
                faq.setUserId(faqEntity.getUserId());
                faq.setStatus(faqEntity.getStatus().name());
                faq.setCreatedAt(faqEntity.getCreatedAt().toString());
                faq.setUpdatedAt(faqEntity.getUpdatedAt().toString());
                faq.setSources(fileRepository.findByOwnerIdAndOwnerType(faqEntity.getId(), FileOwnerType.FAQ.name()));
                return faq;
            }).collect(Collectors.toList());

            GetFaqsResponse response = new GetFaqsResponse();
            response.setTotalPage(faqPage.getTotalPages());
            response.setTotalElements(faqPage.getTotalElements());
            response.setData(faqs);

            return response;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
