package com.example.springboot_pay_split.service;

import com.example.springboot_pay_split.repository.PaymentRepository;
import com.example.springboot_pay_split.repository.SettlementRecordsRepository;
import com.example.springboot_pay_split.repository.TaxRecordsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final PaymentRepository paymentRepository;
    private final SettlementRecordsRepository settlementRepository;
    private final TaxRecordsRepository taxRepository;

    // metodo criação de diretório
    private Path prepararDiretorioSaida() throws IOException {
        Path outputDir = Paths.get("outputs");

        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
            log.info("[RELATÓRIOS] Pasta /outputs criada com sucesso.");
        }

        return outputDir;
    }

    public void gerarRelatorios(JobExecution jobExecution){
        try {
            Path outputDir = prepararDiretorioSaida();
            log.info("[RELATÓRIOS] Diretório de saída pronto: {}", outputDir.toAbsolutePath());

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));

             gerarRelatorioAnalitico(outputDir, timestamp);
             gerarRelatorioSintetico(outputDir, timestamp, jobExecution);

        } catch (IOException e) {
            log.error("[RELATÓRIOS] Erro ao preparar o diretório de saída: {}", e.getMessage());
        }
    }


    private void gerarRelatorioAnalitico(Path outputDir, String timestamp) throws IOException {
        Path filePath = outputDir.resolve("analitico_" + timestamp + ".csv");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("external_id;amount_gross;amount_tax;status_processamento;data_hora_execucao\n");

            String agora = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            // Consulta todos os pagamentos salvos para o detalhamento
            paymentRepository.findAll().forEach(payment -> {
                try {
                    writer.write(String.format("%s;%s;%s;%s;%s\n",
                            payment.getExternalId(),
                            payment.getAmountGross(),
                            payment.getAmountTax(),
                            payment.getStatus(),
                            agora
                    ));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void gerarRelatorioSintetico(Path outputDir, String timestamp, JobExecution jobExecution) throws IOException {
        Path filePath = outputDir.resolve("sintetico_" + timestamp + ".csv");

        // Consolidação financeira via queries simples no banco
        BigDecimal totalBruto = paymentRepository.findAll().stream()
                .map(p -> p.getAmountGross() != null ? p.getAmountGross() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalImpostos = taxRepository.findAll().stream()
                .map(t -> t.getAmountTax() != null ? t.getAmountTax() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLiquido = settlementRepository.findAll().stream()
                .map(s -> s.getAmountNet() != null ? s.getAmountNet() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Mapeamento das estatísticas do Spring Batch
        long transacoesNovas = 0;
        long transacoesIgnoradasOuSkip = 0;

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            transacoesNovas += stepExecution.getWriteCount();
            transacoesIgnoradasOuSkip += stepExecution.getProcessSkipCount() + stepExecution.getReadSkipCount();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("Métrica;Valor\n");
            writer.write("Total Bruto Processado;" + totalBruto + "\n");
            writer.write("Total Líquido a Pagar;" + totalLiquido + "\n");
            writer.write("Total Impostos Retidos;" + totalImpostos + "\n");
            writer.write("Transações Novas (Gravadas);" + transacoesNovas + "\n");
            writer.write("Transações Ignoradas/Invalidadas;" + transacoesIgnoradasOuSkip + "\n");
        }
    }
}
