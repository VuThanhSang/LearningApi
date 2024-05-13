package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.faq.CreateFaqRequest;
import com.example.learning_api.dto.request.faq.UpdateFaqRequest;
import com.example.learning_api.entity.sql.database.FAQEntity;
import com.example.learning_api.repository.database.ClassRoomRepository;
import com.example.learning_api.repository.database.CourseRepository;
import com.example.learning_api.repository.database.FAQRepository;
import com.example.learning_api.repository.database.UserRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IFaqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaqService implements IFaqService {
    private final ModelMapperService modelMapperService;
    private final FAQRepository faqRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;

    @Override
    public void createFaq(CreateFaqRequest createFaqRequest) {
        try {
            if (createFaqRequest.getQuestion() == null) {
                throw new IllegalArgumentException("Question is required");
            }
            if (createFaqRequest.getTargetId() == null) {
                throw new IllegalArgumentException("CourseId is required");
            }
            if (createFaqRequest.getType().name().equals("COURSE") && courseRepository.findById(createFaqRequest.getTargetId()).isEmpty()) {
                throw new IllegalArgumentException("CourseId is not found");
            }else if (createFaqRequest.getType().name().equals("CLASSROOM") && classRoomRepository.findById(createFaqRequest.getTargetId()).isEmpty()) {
                throw new IllegalArgumentException("ClassRoomId is not found");
            }
            if (createFaqRequest.getUserId() == null) {
                throw new IllegalArgumentException("UserId is required");
            }
            if (userRepository.findById(createFaqRequest.getUserId()).isEmpty()) {
                throw new IllegalArgumentException("UserId is not found");
            }
            FAQEntity faqEntity = modelMapperService.mapClass(createFaqRequest, FAQEntity.class);
            faqEntity.setType(createFaqRequest.getType());
            faqEntity.setCreatedAt(new Date());
            faqEntity.setUpdatedAt(new Date());
            faqRepository.save(faqEntity);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateFaq(UpdateFaqRequest updateFaqRequest) {
        try {
            FAQEntity faqEntity = faqRepository.findById(updateFaqRequest.getId()).orElseThrow(() -> new IllegalArgumentException("Faq not found"));
            if (updateFaqRequest.getQuestion() != null)
                faqEntity.setQuestion(updateFaqRequest.getQuestion());
            faqEntity.setUpdatedAt(new Date());
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
