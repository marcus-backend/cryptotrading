package com.marcus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class HuobiCoin {
    private String symbol;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private Double amount;
    private Double vol;
    private Integer count;
    private BigDecimal bid;
    private Double bidSize;
    private BigDecimal ask;
    private Double askSize;
}
