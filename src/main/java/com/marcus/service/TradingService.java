package com.marcus.service;

import com.marcus.dto.request.TradeRequest;
import com.marcus.dto.response.PageResponse;
import com.marcus.dto.response.TradeResponse;
import com.marcus.dto.response.TransactionResponse;
import com.marcus.model.auth.User;
import com.marcus.model.core.Wallet;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TradingService {
    TradeResponse tradeCrypto(TradeRequest request, User user);
    PageResponse<List<TransactionResponse>> getTransactions(Long userId, Pageable pageable);
    Wallet getWalletBalance(Long userId);
}

