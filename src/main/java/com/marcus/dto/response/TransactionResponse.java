package com.marcus.dto.response;

import com.marcus.model.core.Transaction;
import com.marcus.util.OrderType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponse {
    private Long id;
    private String cryptoPair;
    private OrderType type;
    private BigDecimal amount;
    private BigDecimal price;
    private LocalDateTime timestamp;
    private String coinSymbol; // Simplified Coin representation
    private String walletCurrency; // Simplified Wallet representation

    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.cryptoPair = transaction.getCryptoPair();
        this.type = transaction.getType();
        this.amount = transaction.getAmount();
        this.price = transaction.getPrice();
        this.timestamp = transaction.getTimestamp();
        this.coinSymbol = transaction.getCoin() != null ? transaction.getCoin().getSymbol() : null;
        this.walletCurrency = transaction.getWallet() != null ? transaction.getWallet().getCurrency() : null;
    }
}