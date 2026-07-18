package com.example.springboot_pay_split.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlement_records")
@Getter
@Setter
public class SettlementRecordsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    @Column(name = "merchant_name", nullable = false, length = 150)
    private String merchantName;

    @Column(name = "amount_net", nullable = false)
    private BigDecimal amountNet; // Valor calculado: Gross - Tax

    @Column(name = "receiver_document", nullable = false)
    private String receiverDocument;

    @Column(name = "receiver_bank_code", nullable = false, length = 3)
    private String receiverBankCode;

    @Column(name = "receiver_agency", nullable = false, length = 4)
    private String receiverAgency;

    @Column(name = "receiver_account", nullable = false, length = 12)
    private String receiverAccount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}