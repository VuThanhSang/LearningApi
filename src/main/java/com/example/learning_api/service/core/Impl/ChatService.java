package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.chat.CreateChatRequest;
import com.example.learning_api.dto.request.chat.UpdateChatRequest;
import com.example.learning_api.entity.sql.database.ChatEntity;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.repository.database.ChatRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TeacherRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService implements IChatService {
    private final ModelMapperService modelMapperService;
    private final ChatRepository chatRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    @Override
    public void createChat(CreateChatRequest body) {
        try{
            if (body.getSenderId()==null){
                throw new IllegalArgumentException("SenderId is required");
            }
            if (body.getRole() == RoleEnum.USER.name()){
                if (studentRepository.findById(body.getSenderId()) == null){
                    throw new IllegalArgumentException("SenderId is not found");
                }
            }else{
                if (teacherRepository.findById(body.getSenderId()) == null){
                    throw new IllegalArgumentException("SenderId is not found");
                }
            }
            ChatEntity chatEntity = modelMapperService.mapClass(body, ChatEntity.class);
            chatEntity.setCreatedAt(new Date());
            chatEntity.setUpdatedAt(new Date());
            chatRepository.save(chatEntity);
        }
        catch (Exception e) {
            log.error("Error in createChat: ", e);
            throw new IllegalArgumentException(e.getMessage());

        }

    }

    @Override
    public void deleteChat(String chatId) {
        try{
            ChatEntity chatEntity = chatRepository.findById(chatId).orElse(null);
            if (chatEntity == null){
                throw new IllegalArgumentException("ChatId is not found");
            }
            chatRepository.deleteById(chatId);
        }
        catch (Exception e) {
            log.error("Error in deleteChat: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateChat(UpdateChatRequest body) {
        try {
            ChatEntity chatEntity = chatRepository.findById(body.getId()).orElse(null);
            if (chatEntity == null) {
                throw new IllegalArgumentException("ChatId is not found");
            }
            chatEntity = modelMapperService.mapClass(body, ChatEntity.class);
            chatEntity.setUpdatedAt(new Date());
            chatRepository.save(chatEntity);
        }
        catch (Exception e) {
            log.error("Error in updateChat: ", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
