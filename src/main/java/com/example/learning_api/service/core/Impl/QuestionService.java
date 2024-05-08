package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.question.CreateQuestionRequest;
import com.example.learning_api.dto.request.question.UpdateQuestionRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.question.CreateQuestionResponse;
import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import com.example.learning_api.entity.sql.database.QuestionEntity;
import com.example.learning_api.entity.sql.database.TestEntity;
import com.example.learning_api.enums.QuestionType;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.QuestionRepository;
import com.example.learning_api.repository.database.TestRepository;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IQuestionService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
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
public class QuestionService implements IQuestionService {

    private final ModelMapperService modelMapperService;
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public CreateQuestionResponse createQuestion(CreateQuestionRequest body) {
        try{
            TestEntity testEntity = testRepository.findById(body.getTestId())
                    .orElseThrow(() -> new IllegalArgumentException("Test not found"));
            if (!ImageUtils.isValidImageFile(body.getSource())&&body.getSource()!=null) {
                throw new CustomException(ErrorConstant.IMAGE_INVALID);
            }
            if (body.getTestId()==null){
                throw new IllegalArgumentException("TestId is required");

            }
            if (testEntity==null){
                throw new IllegalArgumentException("TestId is not found");
            }
            CreateQuestionResponse resData = new CreateQuestionResponse();
            QuestionEntity questionEntity = modelMapperService.mapClass(body, QuestionEntity.class);
            if(body.getSource()!=null){
                byte[] originalImage = new byte[0];
                originalImage = body.getSource().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getContent(), "questions"),
                        newImage,
                        "image"
                );
                questionEntity.setSource(imageUploaded.getUrl());
            }
            questionEntity.setCreatedAt(new Date());
            questionEntity.setUpdatedAt(new Date());
            questionRepository.save(questionEntity);
            resData.setTestId(body.getTestId());
            resData.setCreatedAt(questionEntity.getCreatedAt().toString());
            resData.setContent(body.getContent());
            resData.setDescription(body.getDescription());
            resData.setId(questionEntity.getId());
            resData.setType(body.getType());
            resData.setUpdatedAt(questionEntity.getUpdatedAt().toString());
            return resData;

        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateQuestion(UpdateQuestionRequest body) {
        try{
            QuestionEntity questionEntity = questionRepository.findById(body.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Question not found"));
            if (body.getId()==null){
                throw new IllegalArgumentException("QuestionId is required");
            }
            if (questionEntity==null){
                throw new IllegalArgumentException("QuestionId is not found");
            }
            if(body.getContent()!=null)
                questionEntity.setContent(body.getContent());
            if(body.getDescription()!=null)
                questionEntity.setDescription(body.getDescription());
            if(body.getSource()!=null)
                questionEntity.setSource(body.getSource());
            if(body.getType()!=null)
                questionEntity.setType(QuestionType.valueOf(body.getType()));
            questionEntity.setUpdatedAt(new Date());
            questionRepository.save(questionEntity);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteQuestion(String id) {
        try{
            questionRepository.deleteById(id);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetQuestionsResponse getQuestions(int page, int size, String search) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<QuestionEntity> questionEntities = questionRepository.findByTestId(search, pageAble);
            GetQuestionsResponse resData = new GetQuestionsResponse();
            List<GetQuestionsResponse.QuestionResponse> questionResponses = new ArrayList<>();
            for (QuestionEntity questionEntity : questionEntities){
                GetQuestionsResponse.QuestionResponse questionResponse = modelMapperService.mapClass(questionEntity, GetQuestionsResponse.QuestionResponse.class);
                questionResponses.add(questionResponse);
            }
            resData.setQuestions(questionResponses);
            resData.setTotalElements(questionEntities.getTotalElements());
            resData.setTotalPage(questionEntities.getTotalPages());
            return resData;

        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
