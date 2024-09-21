package com.example.ai_batch1.batch;

import com.example.ai_batch1.service.crypto.MarketTimingService;
import com.example.ai_batch1.service.crypto.VolumeAnalysisService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.Tasklet;

import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;

import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.transaction.PlatformTransactionManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public Job myBatchJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("myBatchJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet lastJobTasklet) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(lastJobTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet lastJobTasklet(VolumeAnalysisService volumeAnalysisService,
                                   MarketTimingService marketTimingService) {
        return (contribution, chunkContext) -> {
            System.out.println("Tasklet 실행 중...");

            // 1. 거래량 분석 및 알림 전송
            volumeAnalysisService.volumeAnalyze();

            // 2. 시장 타이밍 분석 및 알림 전송
            marketTimingService.marketTimingAnalyze();

            return RepeatStatus.FINISHED;
        };
    }
}
