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
import java.util.Optional;

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
            wallet.setCurrency("USDT"); // Assuming USDT as default; adjust if needed
            wallet = walletRepository.save(wallet);
        }
        return wallet;
    }

    @Override
    public Wallet addBalance(Wallet wallet, Long money) {
        BigDecimal balance = wallet.getBalance();
        BigDecimal newBalance = balance.add(BigDecimal.valueOf(money));
        wallet.setBalance(newBalance);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findWalletById(Long id) {
        Optional<Wallet> wallet = walletRepository.findById(id);
        if (wallet.isPresent()) {
            return wallet.get();
        }
        throw new BusinessException("Wallet not found!");
    }

    @Override
    public Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, Long amount) {
        Wallet senderWallet = getUserWallet(sender);
        BigDecimal amountBD = BigDecimal.valueOf(amount);
        if (senderWallet.getBalance().compareTo(amountBD) < 0) {
            throw new BusinessException("Insufficient balance...");
        }

        BigDecimal senderBalance = senderWallet.getBalance().subtract(amountBD);
        senderWallet.setBalance(senderBalance);
        walletRepository.save(senderWallet);

        BigDecimal receiverBalance = receiverWallet.getBalance().add(amountBD);
        receiverWallet.setBalance(receiverBalance);
        walletRepository.save(receiverWallet);

        return senderWallet;
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
        } else { // SELL
            newBalance = wallet.getBalance().add(order.getPrice());
        }

        wallet.setBalance(newBalance);
        return walletRepository.save(wallet);
    }
}
