package com.example.ai_batch1.batch;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob(QuartzJob.class)
                .withIdentity("quartzJob")
                .withDescription("Invoke Spring Batch Job through Quartz")
                .storeDurably() // If true, the job will remain stored even if no triggers point to it
                .build();
    }

    @Bean
    public Trigger cronTrigger(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("quartzTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?") // 매일 자정에 실행
                    .withMisfireHandlingInstructionDoNothing())
                .build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(Trigger cronTrigger, JobDetail jobDetail) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setJobDetails(jobDetail);
        schedulerFactory.setTriggers(cronTrigger);
        return schedulerFactory;
    }
}
