package com.marcus.service;

import com.marcus.model.core.Coin;
import com.marcus.util.OrderType;

import java.math.BigDecimal;

public interface CoinService {

    void fetchAndSaveCoinData();

    Coin findById(Long coinId);

    BigDecimal getBestAggregatedPrice(String symbol, OrderType type);
}
