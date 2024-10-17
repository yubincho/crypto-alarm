package com.example.ai_batch1.batch;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
public class QuartzJob implements Job, ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            // ApplicationContext에서 Spring 빈 가져오기
            JobLauncher jobLauncher = context.getBean(JobLauncher.class);
            org.springframework.batch.core.Job myBatchJob = context.getBean("myBatchJob", org.springframework.batch.core.Job.class);

            // Job 실행
            jobLauncher.run(myBatchJob, new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())  // Job Parameters 설정
                    .toJobParameters());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}

