package com.example.springboot_pay_split.batch.writer;

import com.example.springboot_pay_split.domain.Transaction;
import com.example.springboot_pay_split.model.PaymentEntity;
import com.example.springboot_pay_split.model.SettlementRecordsEntity;
import com.example.springboot_pay_split.model.TaxRecordsEntity;
import com.example.springboot_pay_split.repository.PaymentRepository;
import com.example.springboot_pay_split.repository.SettlementRecordsRepository;
import com.example.springboot_pay_split.repository.TaxRecordsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class PaymentItemWriter implements ItemWriter<Transaction> {

    private final PaymentRepository paymentRepository;
    private final SettlementRecordsRepository settlementRepository;
    private final TaxRecordsRepository taxRepository;

    public PaymentItemWriter(PaymentRepository paymentRepository, SettlementRecordsRepository settlementRepository, TaxRecordsRepository taxRepository) {
        this.paymentRepository = paymentRepository;
        this.settlementRepository = settlementRepository;
        this.taxRepository = taxRepository;
    }

    @Transactional
    public void write(Chunk<? extends Transaction> chunk) throws Exception {
        for (Transaction dto : chunk) {
            UUID externalUuid = UUID.fromString(String.valueOf(dto.externalId()));

            // Idempotência: Verifica se a transação já existe no banco de dados
            if (paymentRepository.findByExternalId(dto.externalId()).isPresent()) {
                log.warn("Transaction already exists {}; it will be ignored.", dto.externalId());
                continue;
            }
            // Salvar na tabela de pagamentos
            PaymentEntity payment = new PaymentEntity();
            payment.setExternalId(externalUuid);
            payment.setAmountGross(dto.amountGross());
            payment.setAmountTax(dto.amountTax());
            payment.setStatus("PROCESSED");

            PaymentEntity savedPayment = paymentRepository.save(payment); // Salva a entidade de pagamento no banco de dados

            // 3. Salvar Detalhe 1: Liquidação (settlement_records)
            BigDecimal amountNet = dto.amountGross().subtract(dto.amountTax());

            SettlementRecordsEntity settlement = new SettlementRecordsEntity();
            settlement.setPayment(savedPayment);
            settlement.setMerchantName(dto.merchantName());
            settlement.setAmountNet(amountNet);
            settlement.setReceiverDocument(dto.receiverDocument());
            settlement.setReceiverBankCode(dto.receiverBank());
            settlement.setReceiverAgency(dto.receiverAgency());
            settlement.setReceiverAccount(dto.receiverAccount());

            settlementRepository.save(settlement);

            // 4. Salvar Detalhe 2: Imposto (tax_records)
            TaxRecordsEntity tax = new TaxRecordsEntity();
            tax.setPayment(savedPayment);
            tax.setPayerDocument(dto.payerDocument());
            tax.setAmountTax(dto.amountTax());

            taxRepository.save(tax);
        }
    }
}