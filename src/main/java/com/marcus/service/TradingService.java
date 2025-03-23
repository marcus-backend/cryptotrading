package com.marcus.service;

import com.marcus.dto.request.TradeRequest;
import com.marcus.dto.response.TradeResponse;
import com.marcus.model.auth.User;
import com.marcus.model.core.Transaction;
import com.marcus.model.core.Wallet;

import java.util.List;

public interface TradingService {
    TradeResponse tradeCrypto(TradeRequest request, User user);
    List<Transaction> getTransactions(Long userId);
    Wallet getWalletBalance(Long userId);
}

