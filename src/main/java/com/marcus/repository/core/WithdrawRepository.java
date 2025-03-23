package com.marcus.repository.core;

import com.marcus.model.core.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {
    List<Withdraw> findByUserId(Long userId);
}
