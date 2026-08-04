package com.example.springboot_pay_split.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tax_records")
@Getter
@Setter
public class TaxRecordsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    @Column(name = "payer_document", nullable = false)
    private String payerDocument; // CNPJ do pagador que reteve o imposto

    @Column(name = "amount_tax", nullable = false)
    private BigDecimal amountTax; // O valor retido para o governo

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
