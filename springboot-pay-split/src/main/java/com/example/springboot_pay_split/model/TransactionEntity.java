package com.example.springboot_pay_split.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true, length = 36)
    private String externalId;

    @Column(name = "payer_document", nullable = false, length = 14)
    private String payerDocument;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "amount_gross",precision = 15, scale = 2)
    private BigDecimal amountGross;

    @Column(name = "amount_tax",precision = 15, scale = 2)
    private BigDecimal amountTax;

    @Column(name = "legal_invoice_id")
    private Long legalInvoiceId;

    @Column(name = "receiver_document", length = 14)
    private String receiverDocument;

    @Column(name = "receiver_bank")
    private String receiverBank;

    @Column(name = "receiver_agency", length = 10)
    private String receiverAgency;

    @Column(name = "receiver_account", length = 20)
    private String receiverAccount;

    public TransactionEntity() {}


}
