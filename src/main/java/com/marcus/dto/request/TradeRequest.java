package com.marcus.dto.request;

import com.marcus.util.OrderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TradeRequest {
    @NotBlank
    private String symbol;

    @NotNull
    private BigDecimal amount; // Renamed from quantity to match TradingServiceImpl

    @NotNull
    private OrderType tradeType;

    @NotNull
    private BigDecimal price; // Price requested by the user for validation
}