package com.marcus.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderStatus {
    @JsonProperty("pending")
    PENDING,
    @JsonProperty("filed")
    FILED,
    @JsonProperty("cancelled")
    CANCELLED,
    @JsonProperty("partially_field")
    PARTIALLY_FIELD,
    @JsonProperty("error")
    ERROR,
    @JsonProperty("success")
    SUCCESS
}
