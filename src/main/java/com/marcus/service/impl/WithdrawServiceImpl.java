package com.marcus.service.impl;

import com.marcus.exception.BusinessException;
import com.marcus.model.auth.User;
import com.marcus.model.core.Withdraw;
import com.marcus.repository.core.WithdrawRepository;
import com.marcus.service.WithdrawService;
import com.marcus.util.WithdrawStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WithdrawServiceImpl implements WithdrawService {
    private final WithdrawRepository withdrawRepository;

    @Override
    public Withdraw requestWithdraw(Long amount, User user) {
        Withdraw withdraw = new Withdraw();
        withdraw.setAmount(amount);
        withdraw.setUser(user);
        withdraw.setStatus(WithdrawStatus.PENDING);
        return withdrawRepository.save(withdraw);
    }

    @Override
    public Withdraw processWithdraw(Long withdrawId, boolean accepted) {
        Optional<Withdraw> optional = withdrawRepository.findById(withdrawId);

        if (optional.isEmpty()) {
            throw new BusinessException("Withdraw not found");
        }

        Withdraw withdraw = optional.get();
        withdraw.setDate(LocalDateTime.now());

        if (accepted) {
            withdraw.setStatus(WithdrawStatus.SUCCESS);
        } else {
            withdraw.setStatus(WithdrawStatus.PENDING);
        }
        return withdrawRepository.save(withdraw);


    }

    @Override
    public List<Withdraw> getUsersWithdrawHistory(User user) {
        return withdrawRepository.findByUserId(user.getId());
    }

    @Override
    public List<Withdraw> getAllWithdrawRequest() {
        return withdrawRepository.findAll();
    }
}
