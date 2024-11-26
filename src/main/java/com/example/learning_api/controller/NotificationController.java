package com.example.learning_api.controller;

import com.example.learning_api.dto.request.notification.SendNotificationRequest;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final INotificationService notificationService;



}
