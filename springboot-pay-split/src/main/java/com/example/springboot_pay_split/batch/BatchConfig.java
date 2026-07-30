package com.example.springboot_pay_split.batch;

import com.example.springboot_pay_split.batch.listener.ValidacaoSkipListener;
import com.example.springboot_pay_split.batch.writer.PaymentItemWriter;
import com.example.springboot_pay_split.domain.Transaction;
import com.example.springboot_pay_split.model.TransactionEntity;
import com.example.springboot_pay_split.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BatchConfig {

    private final TransactionRepository transactionRepository;

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("processarTransacoes", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public Step lerTransacoesStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  ItemReader<Transaction> transactionItemReader,
                                  BeanValidatingItemProcessor<Transaction> validador,
                                  PaymentItemWriter paymentItemWriter,
                                  ValidacaoSkipListener validacaoSkipListener) {
        return new StepBuilder("lerTransacoesArquivo", jobRepository)
                .<Transaction, Transaction>chunk(100, transactionManager)
                .reader(transactionItemReader)
                .processor(validador)
                .writer(paymentItemWriter)
                .faultTolerant()
                .skip(org.springframework.batch.item.validator.ValidationException.class)
                .skipLimit(20000)
                .listener(validacaoSkipListener) // Usa o bean injetado pelo Spring
                .build();
    }

    @Bean
    public BeanValidatingItemProcessor<Transaction> validadorDeTransacao() {
        BeanValidatingItemProcessor<Transaction> processor = new BeanValidatingItemProcessor<>();
        processor.setFilter(false); // usei como false para conseguir obter log o motivo da falha atraves de um exception
        return processor;
    }
}

