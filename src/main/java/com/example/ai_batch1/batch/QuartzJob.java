package com.example.ai_batch1.batch;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class QuartzJob implements Job {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private org.springframework.batch.core.Job myBatchJob;  // Assume you have a Spring Batch job bean

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // You can set job parameters here
            jobLauncher.run(myBatchJob, new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())  // Pass current timestamp
                    .toJobParameters());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
