package com.marcus.model.core;

import com.marcus.model.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_coins")
public class Coin extends AbstractEntity<Long> {
    private String symbol;
    private BigDecimal bidPrice; // Best bid price (for sell orders)
    private Double bidQty;
    private BigDecimal askPrice; // Best ask price (for buy orders)
    private Double askQty;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private Double amount;
    private Double volume;
    private Integer count;
    private Double bidSize;
    private Double askSize;
}
