package com.marcus.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum WithdrawStatus {
    @JsonProperty("pending")
    PENDING,
    @JsonProperty("success")
    SUCCESS,
    @JsonProperty("decline")
    DECLINE,

}
