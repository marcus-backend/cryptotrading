package com.marcus.model.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.marcus.model.AbstractEntity;
import com.marcus.model.auth.User;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(name = "tbl_wallet")
public class Wallet extends AbstractEntity<Long> {
    @OneToOne
    private User user;
    private BigDecimal balance;
    private String currency;
}
