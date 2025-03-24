package com.marcus.repository.core;

import com.marcus.dto.response.PageResponse;
import com.marcus.model.core.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserUsername(String username);
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
}
