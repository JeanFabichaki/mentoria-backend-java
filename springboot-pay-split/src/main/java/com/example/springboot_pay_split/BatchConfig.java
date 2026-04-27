package com.example.springboot_pay_split;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("processarTransacoes", jobRepository)
                .start(step)
                .build();
    }

//    @Bean
//    public Step processarLote(JobRepository jobRepository, PlatformTransactionManager transactionManager){
//        return new StepBuilder("get-csv", jobRepository)
//                .tasklet((contribution, chunkContext) -> {
//                    System.out.println("Obtendo arquivo CSV");
//                    return RepeatStatus.FINISHED;
//                }, transactionManager )
//                .build();
//    }
//    FlatFileItemReader
    @Bean
    public Step lerTransacoesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, ItemReader<Transaction> transactionItemReader){
        return new StepBuilder("lerTransacoesArquivo", jobRepository)
                .<Transaction,Transaction>chunk(100,transactionManager)
                .reader(transactionItemReader)
                .writer(chunk -> {
                    for(Transaction transaction : chunk) {
                        System.out.println("Lendo transacao: " + transaction.getExternalId());
                    }
                })
                .build();
    }
}

