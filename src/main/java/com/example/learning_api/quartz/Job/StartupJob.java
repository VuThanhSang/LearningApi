package com.example.learning_api.quartz.Job;
import com.example.learning_api.dto.request.deadline.UpdateDeadlineRequest;
import com.example.learning_api.dto.request.test.UpdateTestRequest;
import com.example.learning_api.entity.sql.database.DeadlineEntity;
import com.example.learning_api.entity.sql.database.TestEntity;
import com.example.learning_api.enums.DeadlineStatus;
import com.example.learning_api.enums.TestStatus;
import com.example.learning_api.quartz.Schedules.DeadlineSchedulerService;
import com.example.learning_api.quartz.Schedules.TestSchedulerService;
import com.example.learning_api.repository.database.StudentEnrollmentsRepository;
import com.example.learning_api.service.core.IDeadlineService;
import com.example.learning_api.service.core.ITestService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.functions.T;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RequiredArgsConstructor
public class StartupJob implements Job {
    private final ITestService testService;
    private final TestSchedulerService testSchedulerService;
    private final IDeadlineService deadlineService;
    private final DeadlineSchedulerService deadlineSchedulerService;
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private static final Logger logger = LoggerFactory.getLogger(StartupJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        try{


            logger.info("The startup job is executed.");
            System.out.println("Hello World 2");
        }
        catch (Exception e){
            logger.error("Error when executing the startup job: " + e.getMessage());
        }

    }
}