package com.marcus.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {
    private Long id;
    private Long userId;
    private Long coinId;
    private String coinSymbol;
    private Long walletId;
    private String cryptoPair;
    private String type;
    private BigDecimal amount;
    private BigDecimal price;
    private LocalDateTime timestamp;
}
