package com.marcus.model.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.marcus.model.AbstractEntity;
import com.marcus.util.WalletTransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(name = "tbl_wallet_transaction")
public class WalletTransaction extends AbstractEntity<Long> {
    @ManyToOne
    private Wallet wallet;
    private WalletTransactionType type;
    private LocalDate date;
    private String transferId;
    private String purpose;
    private Long amount;
}
