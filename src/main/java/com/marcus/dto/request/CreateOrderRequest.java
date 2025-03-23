package com.marcus.dto.request;

import com.marcus.util.OrderType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
    private Long coinId;
    private double quantity;
    private OrderType orderType;
}
