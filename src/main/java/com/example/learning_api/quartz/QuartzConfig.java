package com.example.learning_api.quartz;

import com.example.learning_api.quartz.Job.StartupJob;
import jakarta.annotation.PostConstruct;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Configuration
public class QuartzConfig {

    private final AutowireCapableBeanFactory beanFactory;

    public QuartzConfig(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean
    public JobFactory jobFactory() {
        return new JobFactory() {
            @Override
            public org.quartz.Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws org.quartz.SchedulerException {
                Object job = beanFactory.createBean(bundle.getJobDetail().getJobClass());
                if (job instanceof org.quartz.Job) {
                    return (org.quartz.Job) job;
                }
                throw new org.quartz.SchedulerException("Job class does not implement Quartz Job interface.");
            }
        };
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setJobFactory(jobFactory); // Inject custom JobFactory if needed
        schedulerFactory.setWaitForJobsToCompleteOnShutdown(true);
        return schedulerFactory;
    }

}
