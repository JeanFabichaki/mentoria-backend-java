package com.example.springboot_pay_split;

import com.example.springboot_pay_split.domain.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;

@Slf4j
public class ValidacaoSkipListener implements SkipListener<Transaction, Transaction> {

    @Override
    public void onSkipInProcess(Transaction item, Throwable t) {
        log.warn("==> ITEM INVALIDO PULADO: ID {} | Motivo: {}", item.externalId(), t.getMessage());
    }

    @Override
    public void onSkipInRead(Throwable t) {
    }

    @Override
    public void onSkipInWrite(Transaction item, Throwable t) {
    }
}
