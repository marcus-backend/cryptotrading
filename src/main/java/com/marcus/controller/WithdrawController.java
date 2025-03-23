package com.marcus.controller;

import com.marcus.model.auth.User;
import com.marcus.model.core.Wallet;
import com.marcus.model.core.Withdraw;
import com.marcus.service.UserService;
import com.marcus.service.WalletService;
import com.marcus.service.WithdrawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/withdraw")
@Validated
@Slf4j
@Tag(name = "Withdraw Controller")
@RequiredArgsConstructor
public class WithdrawController {
    private final WithdrawService withdrawService;
    private final WalletService walletService;
    private final UserService userService;

    @Operation(summary = "", description = "")
    @PostMapping("/{userId}/{amount}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addUser(
            @PathVariable Long userId,
            @PathVariable Long amount
    ) {
        User user = userService.getUserById(userId);
        Wallet userWallet = walletService.getUserWallet(user);

        Withdraw withdraw = withdrawService.requestWithdraw(amount, user);
        walletService.addBalance(userWallet, -withdraw.getAmount());
        return new ResponseEntity<>(withdraw, HttpStatus.OK);
    }

    @Operation(summary = "", description = "")
    @PatchMapping("/{userId}/{id}/proceed/{accept}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> processWithdraw(
            @PathVariable Long userId,
            @PathVariable Long id,
            @PathVariable boolean accept
    ) {
        User user = userService.getUserById(userId);
        Withdraw withdraw = withdrawService.processWithdraw(id, accept);

        Wallet userWallet = walletService.getUserWallet(user);
        if (!accept) {
            walletService.addBalance(userWallet, withdraw.getAmount());
        }

        return new ResponseEntity<>(withdraw, HttpStatus.OK);
    }

    @Operation(summary = "Get user", description = "API get a user")
    @GetMapping("/{userId}/withdraw")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllWithdrawRequest(@PathVariable long userId) {
        User user = userService.getUserById(userId);
        List<Withdraw> withdraws = withdrawService.getAllWithdrawRequest();
        return new ResponseEntity<>(withdraws, HttpStatus.OK);


    }


}
