package com.example.learning_api.quartz.Schedules;

import com.example.learning_api.quartz.Job.StartupJob;
import jakarta.annotation.PostConstruct;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuartzStartupService {

    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    public void startUpJob() throws SchedulerException {
        // Tạo JobDetail
        JobDetail jobDetail = JobBuilder.newJob(StartupJob.class)
                .withIdentity("StartupJob", "Default")
                .build();

        // Tạo Trigger - Trigger sẽ được khởi chạy ngay khi ứng dụng bắt đầu
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("StartupJobTrigger", "Default")
                .startNow() // Bắt đầu ngay lập tức khi ứng dụng khởi động
                .build();

        // Lên lịch job
        scheduler.scheduleJob(jobDetail, trigger);
    }
}