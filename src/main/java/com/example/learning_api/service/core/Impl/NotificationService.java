//package com.example.learning_api.service.core.Impl;
//
//import com.example.learning_api.kafka.KafkaConstant;
//import com.example.learning_api.kafka.message.NotificationMsgData;
//import com.example.learning_api.entity.sql.database.*;
//import com.example.learning_api.enums.NotificationFormType;
//import com.example.learning_api.enums.RoleEnum;
//import com.example.learning_api.kafka.publisher.NotificationKafkaPublisher;
//import com.example.learning_api.repository.database.*;
//import com.example.learning_api.service.common.ModelMapperService;
//import com.example.learning_api.service.core.INotificationService;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.function.Supplier;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class NotificationService implements INotificationService {
//    private final NotificationRepository notificationRepository;
//    private final ModelMapperService modelMapperService;
//    private final StudentRepository studentRepository;
//    private final TeacherRepository teacherRepository;
//    private final ForumRepository forumRepository;
//    private final ForumCommentRepository forumCommentRepository;
//    private final ClassRoomRepository classRoomRepository;
//    private final TestRepository testRepository;
//    private final DeadlineRepository deadlineRepository;
//    private final StudentEnrollmentsRepository studentEnrollmentRepository;
//    private final NotificationKafkaPublisher notificationKafkaPublisher;
//
//    private static final String ENTITY_NOT_FOUND = "%s not found";
//
//    @Override
//    public void sendNotification(NotificationMsgData body) {
//        try {
//            validateData(body);
//            switch (NotificationFormType.valueOf(body.getFormType())) {
//                case FORUM_VOTE:
//                case FORUM_COMMENT:
//                    notifyForum(body);
//                    break;
//                case CLASS:
//                case TEST:
//                    notifyTestOrClass(body);
//                    break;
//                default:
//                    throw new IllegalArgumentException("Unsupported form type: " + body.getFormType());
//            }
//        } catch (Exception e) {
//            log.error("Error sending notification", e);
//            throw new IllegalArgumentException("Failed to send notification: " + e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional
//    public void saveNotification(NotificationMsgData request) {
//        NotificationEntity notificationEntity = modelMapperService.mapClass(request, NotificationEntity.class);
//        String currentTime = String.valueOf(System.currentTimeMillis());
//        notificationEntity.setCreatedAt(currentTime);
//        notificationEntity.setUpdatedAt(currentTime);
//        notificationRepository.save(notificationEntity);
//    }
//
//    private void validateData(NotificationMsgData body) {
//        validateSender(body.getSenderId(), body.getSenderRole());
//        validateFormType(body.getFormType(), body.getFormId());
//    }
//
//    private void validateSender(String senderId, String senderRole) {
//        if (RoleEnum.USER.name().equals(senderRole)) {
//            findEntityOrThrow(() -> studentRepository.findById(senderId), "Student");
//        } else if (RoleEnum.TEACHER.name().equals(senderRole)) {
//            findEntityOrThrow(() -> teacherRepository.findById(senderId), "Teacher");
//        } else {
//            throw new IllegalArgumentException("Invalid user role: " + senderRole);
//        }
//    }
//
//    private void validateFormType(String formType, String formId) {
//        switch (NotificationFormType.valueOf(formType)) {
//            case FORUM_VOTE:
//                findEntityOrThrow(() -> forumRepository.findById(formId), "Forum");
//                break;
//            case FORUM_COMMENT:
//                findEntityOrThrow(() -> forumCommentRepository.findById(formId), "Forum comment");
//                break;
//            case CLASS:
//                findEntityOrThrow(() -> classRoomRepository.findById(formId), "Class room");
//                break;
//            case TEST:
//                findEntityOrThrow(() -> testRepository.findById(formId), "Test");
//                break;
//            case DEADLINE:
//                findEntityOrThrow(() -> deadlineRepository.findById(formId), "Deadline");
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid form type: " + formType);
//        }
//    }
//
//    private <T> T findEntityOrThrow(Supplier<java.util.Optional<T>> finder, String entityName) {
//        return finder.get().orElseThrow(() -> new IllegalArgumentException(String.format(ENTITY_NOT_FOUND, entityName)));
//    }
//
//    private void notifyForum(NotificationMsgData body) {
//        ForumEntity forumEntity = findEntityOrThrow(() -> forumRepository.findById(body.getFormId()), "Forum");
//        NotificationMsgData notificationMsgData = new NotificationMsgData();
//        notificationMsgData.setSenderId(forumEntity.getAuthorId());
//        notificationMsgData.setSenderRole(String.valueOf(forumEntity.getRole()));
//        notificationMsgData.setTitle(NotificationFormType.FORUM_VOTE.name().equals(body.getFormType()) ?
//                "New vote for your forum" : "New comment for your forum");
//        notificationMsgData.setMessage(NotificationFormType.FORUM_VOTE.name().equals(body.getFormType()) ?
//                "New vote for your forum" : body.getMessage());
//        notificationMsgData.setFormId(body.getFormId());
//        notificationMsgData.setFormType(body.getFormType());
//        notificationMsgData.setType(body.getType());
//        notificationKafkaPublisher.sendNotification(notificationMsgData);
//    }
//
//    private void notifyTestOrClass(NotificationMsgData body) {
//        TestEntity testEntity = findEntityOrThrow(() -> testRepository.findById(body.getFormId()), "Test");
//        int pageSize = 100;
//        int pageNumber = 0;
//        Page<StudentEnrollmentsEntity> enrollmentsPage;
//
//        do {
//            Pageable pageable = PageRequest.of(pageNumber, pageSize);
//            enrollmentsPage = studentEnrollmentRepository.findByClassroomId(testEntity.getClassroomId(), pageable);
//
//            for (StudentEnrollmentsEntity enrollment : enrollmentsPage.getContent()) {
//                NotificationMsgData notificationMsgData = NotificationMsgData.builder()
//                        .senderId(enrollment.getStudentId())
//                        .senderRole(RoleEnum.USER.name())
//                        .title(KafkaConstant.TEST_NOTIFICATION_TITLE)
//                        .message(KafkaConstant.TEST_NOTIFICATION_MESSAGE)
//                        .formId(body.getFormId())
//                        .formType(body.getFormType())
//                        .type(body.getType())
//                        .build();
//                notificationKafkaPublisher.sendNotification(notificationMsgData);
//            }
//
//            pageNumber++;
//        } while (enrollmentsPage.hasNext());
//    }
//
//}