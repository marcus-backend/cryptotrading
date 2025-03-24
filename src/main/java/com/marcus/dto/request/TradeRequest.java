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
    @NotBlank(message = "Symbol must not be blank!")
    private String symbol;

    @NotNull(message = "Amount must not be null!")
    private BigDecimal amount;

    @NotNull(message = "TradeType must not be null")
    private OrderType tradeType;

    @NotNull(message = "Price must not be null")
    private BigDecimal price;
}