package com.example.learning_api.quartz.Job;

import com.example.learning_api.entity.sql.database.DeadlineEntity;
import com.example.learning_api.entity.sql.database.NotificationEntity;
import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.enums.NotificationPriority;
import com.example.learning_api.repository.database.DeadlineRepository;
import com.example.learning_api.repository.database.StudentEnrollmentsRepository;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.service.core.INotificationService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class DeadlineReminderJob implements Job {
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final INotificationService notificationService;
    private final  StudentRepository studentRepository;
    private final DeadlineRepository deadlineRepository;
    private static final Logger logger = LoggerFactory.getLogger(TestReminderJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String deadlineId = jobExecutionContext.getJobDetail().getJobDataMap().getString("deadlineId");
        String deadlineTitle = jobExecutionContext.getJobDetail().getJobDataMap().getString("deadlineTitle");
        DeadlineEntity deadlineEntity = deadlineRepository.findById(deadlineId).orElse(null);
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setAuthorId(deadlineId);
        notificationEntity.setPriority(NotificationPriority.NORMAL);
        notificationEntity.setTargetUrl(deadlineId);
        if (jobExecutionContext.getJobDetail().getJobDataMap().getString("role").equals("TEACHER")){
            notificationEntity.setNotificationSettingId("674473d53e126c2148ce1acc");
            notificationEntity.setTitle("Notification Deadline is expired");
            notificationEntity.setMessage("Deadline " + deadlineTitle + " is expired");
            List<String> ids = new ArrayList<>();
            if (deadlineEntity == null) {
                return;
            }
            notificationService.createNotification(notificationEntity, ids);
        }
        else{
            notificationEntity.setNotificationSettingId("674473d53e126c2148ce1ad0");
            notificationEntity.setTitle("Notification Deadline due soon");
            notificationEntity.setMessage("Deadline " + deadlineTitle + " is due soon");
            List<String> studentId = studentEnrollmentsRepository.findStudentsNotTakenDeadline(deadlineEntity.getClassroomId(), deadlineId);
            List<String> userIds = new ArrayList<>();
            for (String id : studentId) {
                StudentEntity studentEntity = studentRepository.findById(id).orElse(null);
                if (studentEntity != null) {
                    userIds.add(studentEntity.getUserId());
                }
            }
            notificationService.createNotification(notificationEntity, userIds);
        }

    }
}
