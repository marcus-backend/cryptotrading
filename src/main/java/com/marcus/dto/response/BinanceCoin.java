package com.marcus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class BinanceCoin {
    private String symbol;
    private BigDecimal bidPrice;
    private Double bidQty;
    private BigDecimal askPrice;
    private Double askQty;
}
