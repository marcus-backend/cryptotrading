package com.marcus.model.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.marcus.model.AbstractEntity;
import com.marcus.model.auth.User;
import com.marcus.util.WithdrawStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Table(name = "tbl_withdraw")
public class Withdraw extends AbstractEntity<Long> {
    private WithdrawStatus status;

    private Long amount;

    @ManyToOne
    private User user;

    private LocalDateTime date = LocalDateTime.now();
}
