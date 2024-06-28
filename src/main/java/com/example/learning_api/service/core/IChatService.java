package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.chat.CreateChatRequest;
import com.example.learning_api.dto.request.chat.UpdateChatRequest;

public interface IChatService {
    void createChat(CreateChatRequest body);
    void deleteChat(String chatId);
    void updateChat(UpdateChatRequest body);
}
