package com.example.learning_api.quartz.Job;

import com.example.learning_api.entity.sql.database.NotificationEntity;
import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TestEntity;
import com.example.learning_api.enums.NotificationPriority;
import com.example.learning_api.repository.database.StudentEnrollmentsRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TestRepository;
import com.example.learning_api.service.core.INotificationService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TestReminderJob implements Job {
    private TestRepository testRepository;
    private StudentEnrollmentsRepository studentEnrollmentsRepository;
    private INotificationService notificationService;
    private StudentRepository studentRepository;
    private static final Logger logger = LoggerFactory.getLogger(TestReminderJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        // Lấy thông tin bài test từ JobDataMap
        String testId = context.getJobDetail().getJobDataMap().getString("testId");
        String testName = context.getJobDetail().getJobDataMap().getString("testName");
        TestEntity testEntity = testRepository.findById(testId).orElse(null);
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setNotificationSettingId("674473d53e126c2148ce1ad0");
        notificationEntity.setTitle("Notification Test due soon");
        notificationEntity.setMessage("Test " + testName + " is due soon");
        notificationEntity.setAuthorId(testId);
        notificationEntity.setPriority(NotificationPriority.NORMAL);
        List<String> studentId = studentEnrollmentsRepository.findStudentsNotTakenTest(testEntity.getClassroomId(), testId);
        List<String> userIds = new ArrayList<>();
        for (String id : studentId) {
            StudentEntity studentEntity = studentRepository.findById(id).orElse(null);
            if (studentEntity != null) {
                userIds.add(studentEntity.getUserId());
            }
        }
        notificationService.createNotification( notificationEntity,userIds);
        logger.info("Trigger reminder for Test: ID = {}, Name = {}", testId, testName);


    }
}