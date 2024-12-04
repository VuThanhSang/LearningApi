package com.example.learning_api.quartz.Schedules;

import com.example.learning_api.entity.sql.database.TestEntity;
import com.example.learning_api.quartz.Job.TestReminderJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TestSchedulerService {

    @Autowired
    private Scheduler scheduler;

    public void scheduleTestReminder(TestEntity test, long offsetInMillis) throws SchedulerException {
        // Tính toán thời gian trigger
        long triggerTimeMillis = Long.parseLong(test.getEndTime()) - offsetInMillis;
        Date triggerTime = new Date(triggerTimeMillis);

        // Tạo JobDetail
        JobDetail jobDetail = JobBuilder.newJob(TestReminderJob.class)
                .withIdentity("TestReminderJob-" + test.getId(), "TestReminderGroup")
                .usingJobData("testId", test.getId())
                .usingJobData("testName", test.getName())
                .build();

        // Tạo Trigger
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("Trigger-" + test.getId(), "TestReminderGroup")
                .startAt(triggerTime) // Thời gian trigger
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();

        // Lên lịch job
        scheduler.scheduleJob(jobDetail, trigger);
    }
}