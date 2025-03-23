package com.marcus.service;

import com.marcus.model.auth.User;
import com.marcus.model.core.Order;
import com.marcus.model.core.Wallet;

public interface WalletService {
    Wallet getUserWallet(User user);
    Wallet addBalance(Wallet wallet, Long money);
    Wallet findWalletById(Long id);
    Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, Long amount);
    Wallet payOrderPayment(Order order, User user);
}
