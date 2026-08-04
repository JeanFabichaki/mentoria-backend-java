package com.example.springboot_pay_split.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private UUID externalId;

    @Column(name = "amount_gross", nullable = false)
    private BigDecimal amountGross;

    @Column(name = "amount_tax", nullable = false)
    private BigDecimal amountTax;

    @Column(name = "status", nullable = false)
    private String status; // PROCESSED, SKIPPED, PROCESSED_WITH_ADJUSTMENT

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist // inserir data/hora de criacao do registro
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}