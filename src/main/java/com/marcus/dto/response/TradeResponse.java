package com.marcus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TradeResponse {
    private Long orderId;
    private String status;
    private String message;
}
