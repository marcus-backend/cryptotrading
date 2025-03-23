package com.marcus.service.impl;

import com.marcus.dto.request.TradeRequest;
import com.marcus.dto.response.TradeResponse;
import com.marcus.exception.BusinessException;
import com.marcus.model.auth.User;
import com.marcus.model.core.Coin;
import com.marcus.model.core.Order;
import com.marcus.model.core.Transaction;
import com.marcus.model.core.Wallet;
import com.marcus.repository.core.CoinRepository;
import com.marcus.repository.core.TransactionRepository;
import com.marcus.repository.core.WalletRepository;
import com.marcus.service.OrderService;
import com.marcus.service.TradingService;
import com.marcus.service.WalletService;
import com.marcus.util.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
public class TradingServiceImpl implements TradingService {
    private final CoinRepository coinRepository;
    private final OrderService orderService;
    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @Override
    public TradeResponse tradeCrypto(TradeRequest request, User user) {
        Coin coin = coinRepository.findBySymbol(request.getSymbol())
                .orElseThrow(() -> new BusinessException("Coin not found"));

        // Determine the current price based on trade type (Ask for BUY, Bid for SELL)
        BigDecimal currentPrice = request.getTradeType() == OrderType.BUY ? coin.getAskPrice() : coin.getBidPrice();

        // Validate requested price against current price (1% threshold)
        BigDecimal requestPrice = request.getPrice();
        BigDecimal threshold = requestPrice.multiply(BigDecimal.valueOf(0.01));
        if (currentPrice.subtract(requestPrice).abs().compareTo(threshold) > 0) {
            throw new BusinessException("Price has changed significantly. Please retry with the updated price: " + currentPrice);
        }

        // Process the order
        Order order = orderService.processOrder(coin, request.getAmount().doubleValue(), request.getTradeType(), user);

        // Update wallet and record transaction
        Wallet wallet = walletService.getUserWallet(user);

        Transaction transaction = Transaction.builder()
                .user(user)
                .coin(coin)
                .wallet(wallet)
                .cryptoPair(request.getSymbol())
                .type(request.getTradeType())
                .amount(request.getAmount())
                .price(currentPrice) // Use current price (ask/bid) instead of request price
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        return new TradeResponse(order.getId(), "SUCCESS", request.getTradeType() + " order completed successfully");
    }

    @Override
    public List<Transaction> getTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Override
    public Wallet getWalletBalance(Long userId) {
        return walletRepository.findByUserId(userId);
    }
}
