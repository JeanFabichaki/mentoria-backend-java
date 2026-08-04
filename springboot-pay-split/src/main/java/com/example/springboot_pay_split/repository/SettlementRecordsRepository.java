package com.example.springboot_pay_split.repository;

import com.example.springboot_pay_split.model.PaymentEntity;
import com.example.springboot_pay_split.model.SettlementRecordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettlementRecordsRepository extends JpaRepository<SettlementRecordsEntity, Long> {
    }
