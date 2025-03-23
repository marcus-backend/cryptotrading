package com.marcus.service;

import com.marcus.model.auth.User;
import com.marcus.model.core.Withdraw;

import java.util.List;

public interface WithdrawService {
    Withdraw requestWithdraw(Long amount, User user);

    Withdraw processWithdraw(Long withdrawId, boolean accepted);

    List<Withdraw> getUsersWithdrawHistory(User user);

    List<Withdraw> getAllWithdrawRequest();
}
