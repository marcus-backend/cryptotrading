package com.marcus.dto.response;

import com.marcus.model.core.Wallet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WalletResponse {
    private String currency;
    private String balance;

    public WalletResponse(Wallet wallet) {
        this.currency = wallet.getCurrency();
        this.balance = wallet.getBalance().toString();
    }
}
