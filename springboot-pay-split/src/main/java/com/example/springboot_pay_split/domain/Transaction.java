package com.example.springboot_pay_split.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.UUID;

import java.math.BigDecimal;

public record Transaction(
        @NotBlank UUID externalId,
        @NotNull String merchantName,
        @NotBlank String payerDocument,
        @NotNull @Positive(message = "Valor da transacao nao pode ser negativo") BigDecimal amountGross,
        @NotNull BigDecimal amountTax,
        Long legalInvoiceId,
        @NotNull String receiverDocument,
        @NotNull String receiverBank,
        @NotNull String receiverAgency,
        @NotNull String receiverAccount
) {
}