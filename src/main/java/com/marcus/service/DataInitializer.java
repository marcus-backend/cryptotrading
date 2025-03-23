package com.marcus.service;

import com.marcus.model.auth.User;
import com.marcus.model.core.Coin;
import com.marcus.model.core.Wallet;
import com.marcus.repository.UserRepository;
import com.marcus.repository.core.CoinRepository;
import com.marcus.repository.core.WalletRepository;
import com.marcus.util.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CoinRepository coinRepository;

    @Override
    public void run(String... args) {
        // Create a test user
        User user = User.builder()
                .username("testuser")
                .password("password")
                .status(UserStatus.ACTIVE)
                .activated(true)
                .build();
        userRepository.save(user);

        // Initialize wallets (50,000 USDT, 0 ETH, 0 BTC)
        Wallet usdtWallet = Wallet.builder()
                .user(user)
                .currency("USDT")
                .balance(new BigDecimal("50000"))
                .build();
        Wallet ethWallet = Wallet.builder()
                .user(user)
                .currency("ETH")
                .balance(BigDecimal.ZERO)
                .build();
        Wallet btcWallet = Wallet.builder()
                .user(user)
                .currency("BTC")
                .balance(BigDecimal.ZERO)
                .build();
        walletRepository.saveAll(List.of(usdtWallet, ethWallet, btcWallet));

        // Initialize coins with placeholder prices (will be updated by scheduler)
        Coin ethCoin = Coin.builder()
                .symbol("ETHUSDT")
                .bidPrice(BigDecimal.ZERO)
                .askPrice(BigDecimal.ZERO)
                .build();
        Coin btcCoin = Coin.builder()
                .symbol("BTCUSDT")
                .bidPrice(BigDecimal.ZERO)
                .askPrice(BigDecimal.ZERO)
                .build();
        coinRepository.saveAll(List.of(ethCoin, btcCoin));
    }
}