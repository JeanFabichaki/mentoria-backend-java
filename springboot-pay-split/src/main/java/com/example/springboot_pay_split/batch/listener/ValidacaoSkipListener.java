package com.example.springboot_pay_split.batch.listener;

import com.example.springboot_pay_split.domain.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidacaoSkipListener implements SkipListener<Transaction, Transaction> {

    @Override
    public void onSkipInProcess(Transaction item, Throwable t) {
        log.warn("==> ITEM INVALIDO PULADO: ID {} | Motivo: {}", item.externalId(), t.getMessage());
    }
}
