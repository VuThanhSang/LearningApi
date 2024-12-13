package com.example.learning_api.quartz.Job;

import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.NotificationPriority;
import com.example.learning_api.repository.database.StudentEnrollmentsRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TeacherRepository;
import com.example.learning_api.repository.database.TestRepository;
import com.example.learning_api.service.core.INotificationService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TestReminderJob implements Job {
    private final TestRepository testRepository;
    private final  StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final INotificationService notificationService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private static final Logger logger = LoggerFactory.getLogger(TestReminderJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        // Lấy thông tin bài test từ JobDataMap
        String testId = context.getJobDetail().getJobDataMap().getString("testId");
        String testName = context.getJobDetail().getJobDataMap().getString("testName");
        TestEntity testEntity = testRepository.findById(testId).orElse(null);
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setAuthorId(testId);
        notificationEntity.setPriority(NotificationPriority.NORMAL);
        notificationEntity.setTargetUrl( testId);
        if ( context.getJobDetail().getJobDataMap().getString("role").equals("TEACHER")){
            notificationEntity.setNotificationSettingId("674473d53e126c2148ce1acc");
            notificationEntity.setTitle("Notification Test is expired");
            notificationEntity.setMessage("Test " + testName + " is expired");
            List<String> ids = new ArrayList<>();
            if (testEntity == null) {
                return;
            }
            TeacherEntity userEntity = teacherRepository.findById(testEntity.getTeacherId()).orElse(null);
            ids.add(userEntity.getUserId());
            notificationService.createNotification(notificationEntity, ids);
        }
        else{
            notificationEntity.setNotificationSettingId("674473d53e126c2148ce1ad0");
            notificationEntity.setTitle("Notification Test due soon");
            notificationEntity.setMessage("Test " + testName + " is due soon");
            List<String> studentId = studentEnrollmentsRepository.findStudentsNotTakenTest(testEntity.getClassroomId(), testId);
            List<String> userIds = new ArrayList<>();
            for (String id : studentId) {
                StudentEntity studentEntity = studentRepository.findById(id).orElse(null);
                if (studentEntity != null) {
                    userIds.add(studentEntity.getUserId());
                }
            }
            notificationService.createNotification( notificationEntity,userIds);

        }

        logger.info("Trigger reminder for Test: ID = {}, Name = {}", testId, testName);


    }
}