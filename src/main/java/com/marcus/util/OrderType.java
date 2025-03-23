package com.marcus.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderType {
    @JsonProperty("buy")
    BUY,
    @JsonProperty("sell")
    SELL
}

