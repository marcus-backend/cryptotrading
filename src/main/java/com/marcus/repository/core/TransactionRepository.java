package com.marcus.repository.core;

import com.marcus.model.core.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserUsername(String username);
    List<Transaction> findByUserId(Long userId);
}
