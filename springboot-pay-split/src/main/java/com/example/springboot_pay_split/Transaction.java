package com.example.springboot_pay_split;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Transaction {
    private String externalId;
    private String merchantName;
    private String payerDocument;
    private BigDecimal amountGross;
    private BigDecimal amountTax;
    private Long legalInvoiceId;
    private String receiverDocument;
    private String receiverBank;
    private String receiverAgency;
    private String receiverAccount;
}
