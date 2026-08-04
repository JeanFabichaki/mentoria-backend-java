package com.example.springboot_pay_split.batch.listener;

import com.example.springboot_pay_split.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportJobListener {

    private final ReportService reportService;

    public void afterJob(JobExecution jobExecution) {
        reportService.gerarRelatorios(jobExecution);
    }
}
