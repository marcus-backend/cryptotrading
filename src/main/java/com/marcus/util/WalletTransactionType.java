package com.marcus.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum WalletTransactionType {
    @JsonProperty("withdraw")
    WITHDRAWAL,
    @JsonProperty("wallet_transfer")
    WALLET_TRANSFER,
    @JsonProperty("add_money")
    ADD_MONEY,
    @JsonProperty("buy_asset")
    BUY_ASSET,
    @JsonProperty("sell_asset")
    SELL_ASSET,
}
