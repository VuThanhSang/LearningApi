package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.answer.CreateAnswerRequest;
import com.example.learning_api.dto.request.answer.UpdateAnswerRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.answer.CreateAnswerResponse;
import com.example.learning_api.entity.sql.database.AnswerEntity;
import com.example.learning_api.entity.sql.database.FileEntity;
import com.example.learning_api.entity.sql.database.QuestionEntity;
import com.example.learning_api.enums.FileOwnerType;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.AnswerRepository;
import com.example.learning_api.repository.database.FileRepository;
import com.example.learning_api.repository.database.QuestionRepository;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IAnswerService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerService implements IAnswerService {
    private final ModelMapperService modelMapperService;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CloudinaryService cloudinaryService;
    private final FileRepository fileRepository;

    @Override
    public CreateAnswerResponse createAnswer(CreateAnswerRequest body) {
        try{
            QuestionEntity questionEntity = questionRepository.findById(body.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("Question not found"));
            if (body.getQuestionId()==null){
                throw new IllegalArgumentException("QuestionId is required");

            }
            if (questionEntity==null){
                throw new IllegalArgumentException("QuestionId is not found");
            }
            CreateAnswerResponse resData = new CreateAnswerResponse();
            AnswerEntity answerEntity = modelMapperService.mapClass(body, AnswerEntity.class);
            FileEntity fileEntity = new FileEntity();
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
                fileEntity.setUrl(imageUploaded.getUrl());
                fileEntity.setType("image");
                fileEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                fileEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                fileEntity.setOwnerType(FileOwnerType.ANSWER);
            }
            answerEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            answerEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            answerRepository.save(answerEntity);
            fileEntity.setOwnerId(answerEntity.getId());
            fileRepository.save(fileEntity);
            resData.setQuestionId(body.getQuestionId());
            resData.setCreatedAt(answerEntity.getCreatedAt().toString());
            resData.setContent(body.getContent());
            resData.setId(answerEntity.getId());
            resData.setCorrect(body.isCorrect());
            resData.setUpdatedAt(answerEntity.getUpdatedAt().toString());
            resData.setSource(fileEntity.getUrl());
            return resData;

        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateAnswer(UpdateAnswerRequest body) {
       try{
              AnswerEntity answerEntity = answerRepository.findById(body.getId())
                     .orElseThrow(() -> new IllegalArgumentException("Answer not found"));
              if (body.getId()==null){
                throw new IllegalArgumentException("AnswerId is required");
              }
              if (answerEntity==null){
                throw new IllegalArgumentException("AnswerId is not found");
              }
              if(body.getContent()!=null)
                answerEntity.setContent(body.getContent());
              answerEntity.setCorrect(body.isCorrect());
              if (body.getSource()!=null){
                  fileRepository.deleteByOwnerIdAndOwnerType(body.getId(), FileOwnerType.ANSWER.name());
                byte[] originalImage = new byte[0];
                originalImage = body.getSource().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getContent(), "questions"),
                        newImage,
                        "image"
                );
                FileEntity fileEntity = new FileEntity();
                fileEntity.setUrl(imageUploaded.getUrl());
                fileEntity.setType("image");
                fileEntity.setOwnerId(body.getId());
                fileEntity.setOwnerType(FileOwnerType.ANSWER);
                fileEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                fileEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                fileRepository.save(fileEntity);
              }
              answerEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
              answerRepository.save(answerEntity);
         }
         catch (Exception e){
              throw new IllegalArgumentException(e.getMessage());
       }

    }

    @Override
    public void deleteAnswer(String id) {
        try{
            answerRepository.deleteById(id);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }
}
