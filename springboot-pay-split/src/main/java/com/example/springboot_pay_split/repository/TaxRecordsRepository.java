package com.example.springboot_pay_split.repository;

import com.example.springboot_pay_split.model.TaxRecordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRecordsRepository extends JpaRepository<TaxRecordsEntity, Long>{
}
