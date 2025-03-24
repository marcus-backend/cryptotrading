package com.marcus.service;

import com.marcus.model.auth.User;
import com.marcus.model.core.Order;
import com.marcus.model.core.Wallet;

public interface WalletService {
    Wallet getUserWallet(User user);
    Wallet payOrderPayment(Order order, User user);
}
