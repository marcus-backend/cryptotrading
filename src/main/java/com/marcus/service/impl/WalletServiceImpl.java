package com.marcus.service.impl;

import com.marcus.exception.BusinessException;
import com.marcus.model.auth.User;
import com.marcus.model.core.Order;
import com.marcus.model.core.Wallet;
import com.marcus.repository.core.WalletRepository;
import com.marcus.service.WalletService;
import com.marcus.util.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;

    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setCurrency("USDT");
            wallet = walletRepository.save(wallet);
        }
        return wallet;
    }

    @Override
    public Wallet payOrderPayment(Order order, User user) {
        Wallet wallet = getUserWallet(user);
        BigDecimal newBalance;

        if (order.getOrderType() == OrderType.BUY) {
            if (wallet.getBalance().compareTo(order.getPrice()) < 0) { // Corrected comparison
                throw new BusinessException("Insufficient funds for this transaction!");
            }
            newBalance = wallet.getBalance().subtract(order.getPrice());
        } else {
            newBalance = wallet.getBalance().add(order.getPrice());
        }

        wallet.setBalance(newBalance);
        return walletRepository.save(wallet);
    }
}
