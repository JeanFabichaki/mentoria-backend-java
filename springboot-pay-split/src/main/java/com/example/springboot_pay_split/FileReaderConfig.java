package com.example.springboot_pay_split;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FileReaderConfig {

    @Bean
    public FlatFileItemReader<Transaction> transactionReader(){
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("transactionItemReader")
                .resource(new ClassPathResource("data/input/input_pagamentos.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(";")
                .names("externalId","merchantName","payerDocument","amountGross","amountTax","legalInvoiceId","receiverDocument","receiverBank","receiverAgency", "receiverAccount")
                .targetType(Transaction.class)
                .build();
    }
}
