package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.question.CreateQuestionRequest;
import com.example.learning_api.dto.request.question.UpdateQuestionRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.question.CreateQuestionResponse;
import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.entity.sql.database.QuestionEntity;
import com.example.learning_api.entity.sql.database.TestEntity;
import com.example.learning_api.entity.sql.database.FeedbackEntity;
import com.example.learning_api.enums.FileOwnerType;
import com.example.learning_api.enums.QuestionType;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.AnswerRepository;
import com.example.learning_api.repository.database.FileRepository;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final AnswerRepository answerRepository;
    private final FileRepository fileRepository;
    public void progressSources(List<MultipartFile> sources, String content, FileEntity fileEntity, QuestionEntity questionEntity){
        if (sources == null) {
            return;

        }
        for (MultipartFile source : sources) {
            try {
                String sourceDto = processSource(source, content);
                fileEntity.setUrl(sourceDto);
                fileEntity.setType("image");
                fileEntity.setExtension("jpg");
                fileEntity.setSize(String.valueOf(source.getSize()));
                fileEntity.setName(content);
                fileEntity.setOwnerType(FileOwnerType.QUESTION);
                fileEntity.setOwnerId(questionEntity.getId());
                fileEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                fileEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                fileRepository.save(fileEntity);
            } catch (IOException e) {
                log.error("Error processing source: " + e.getMessage());
                throw new IllegalArgumentException("Error processing source");
            }
        }

    }
    private String processSource(MultipartFile source, String question) throws IOException {
        byte[] fileBytes = source.getBytes();
        String fileName = StringUtils.generateFileName(question, "forum");
        CloudinaryUploadResponse response;
        byte[] resizedImage = ImageUtils.resizeImage(fileBytes, 400, 400);
        response = cloudinaryService.uploadFileToFolder(CloudinaryConstant.CLASSROOM_PATH, fileName, resizedImage, "image");
        return response.getUrl();
    }
    @Override
    public CreateQuestionResponse createQuestion(CreateQuestionRequest body) {
        try{
            TestEntity testEntity = testRepository.findById(body.getTestId())
                    .orElseThrow(() -> new IllegalArgumentException("Test not found"));
            if (body.getTestId()==null){
                throw new IllegalArgumentException("TestId is required");

            }
            if (testEntity==null){
                throw new IllegalArgumentException("TestId is not found");
            }
            CreateQuestionResponse resData = new CreateQuestionResponse();
            QuestionEntity questionEntity = modelMapperService.mapClass(body, QuestionEntity.class);
            FileEntity fileEntity = new FileEntity();

            questionEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            questionEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            questionEntity.setIndex(questionRepository.findMaxIndexByTestId(body.getTestId())==null?0:questionRepository.findMaxIndexByTestId(body.getTestId())+1);
            questionRepository.save(questionEntity);
            progressSources(body.getSources(), body.getContent(), fileEntity, questionEntity);
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
            progressSources(body.getSources(), body.getContent(), new FileEntity(), questionEntity);
            if(body.getType()!=null)
                questionEntity.setType(QuestionType.valueOf(body.getType()));
            questionEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
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
    public void deleteQuestions(String[] ids) {
        try{
            for (String id : ids){
                questionRepository.deleteById(id);
                answerRepository.deleteByQuestionId(id);
            }
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
