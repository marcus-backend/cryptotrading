package com.marcus.controller;

import com.marcus.config.Translator;
import com.marcus.dto.response.ResponseData;
import com.marcus.model.auth.User;
import com.marcus.model.core.Order;
import com.marcus.model.core.Wallet;
import com.marcus.model.core.WalletTransaction;
import com.marcus.service.AuthenticationService;
import com.marcus.service.OrderService;
import com.marcus.service.UserService;
import com.marcus.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
@Validated
@Slf4j
@Tag(name = "Wallet Controller")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;
    private final UserService userService;
    private final OrderService orderService;
    private final AuthenticationService auth;


    @Operation(summary = "Get user", description = "API get a user")
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<?> getUserWallet(@PathVariable long userId) {
        User user = userService.getByUsername(auth.getUsername());
        return new ResponseData<>(
                HttpStatus.NO_CONTENT.value(),
                Translator.toLocale("user.detail.success"),
                walletService.getUserWallet(user)
        );

    }

    @Operation(summary = "", description = "")
    @PutMapping("/{userId}/{walletId}/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<?> walletToWalletTransfer(
            @PathVariable long userId,
            @PathVariable Long walletId,
            @RequestBody WalletTransaction req
    ) {
        // getUserByToken
        User senderUser = userService.getUserById(userId);
        Wallet receiverWallet = walletService.findWalletById(walletId);
        return new ResponseData<>(
                HttpStatus.CREATED.value(),
                Translator.toLocale("user.add.success"),
                walletService
                        .walletToWalletTransfer(senderUser, receiverWallet, req.getAmount()));
    }


    @Operation(summary = "", description = "")
    @PutMapping("/order/{userId}/{orderId}/pay")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<?> payOrderPayment(
            @PathVariable long userId,
            @PathVariable Long orderId
    ) {
        // getUserByToken
        User user = userService.getUserById(userId);
        Order order = orderService.getOrderById(orderId);
        return new ResponseData<>(
                HttpStatus.CREATED.value(),
                "check me",
                walletService.payOrderPayment(order,user));
    }

}
