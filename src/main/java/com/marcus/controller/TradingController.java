package com.marcus.controller;

import com.marcus.config.Translator;
import com.marcus.dto.request.TradeRequest;
import com.marcus.dto.response.PageResponse;
import com.marcus.dto.response.ResponseData;
import com.marcus.dto.response.ResponseError;
import com.marcus.dto.response.TradeResponse;
import com.marcus.dto.response.TransactionResponse;
import com.marcus.dto.response.WalletResponse;
import com.marcus.exception.BusinessException;
import com.marcus.model.auth.User;
import com.marcus.model.core.Wallet;
import com.marcus.service.AuthenticationService;
import com.marcus.service.TradingService;
import com.marcus.service.UserService;
import com.marcus.util.OrderType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Functional Scope
 * 1.  User able to buy/sell the supported crypto trading pairs
 * 2.  User able to see the list of trading transactions
 * 3.  User able to see the cryptocurrencies wallet balance
 */
@RestController
@RequestMapping("${api.prefix}/trading")
@Validated
@Slf4j
@Tag(name = "Trading Controller")
@RequiredArgsConstructor
public class TradingController {
    private final TradingService tradingService;
    private final AuthenticationService auth;
    private final UserService userService;

    @Operation(summary = "Trade cryptocurrency", description = "API to buy or sell cryptocurrency")
    @PostMapping("/trade")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<?> tradeCrypto(@Valid @RequestBody TradeRequest request) {
        log.info("Request to trade cryptocurrency: symbol={}, type={}", request.getSymbol(), request.getTradeType());
        User user = userService.getByUsername(auth.getUsername());
        if (user == null) {
            throw new BusinessException("User not found!");
        }
        try {
            TradeResponse response = tradingService.tradeCrypto(request, user);
            String successMessage = request.getTradeType() == OrderType.BUY ? "trading.buy.success" : "trading.sell.success";
            return new ResponseData<>(
                    HttpStatus.CREATED.value(),
                    Translator.toLocale(successMessage),
                    response
            );
        } catch (BusinessException e) {
            log.error("Error message: {}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get user's transactions", description = "API to get user's transactions")
    @GetMapping("/transactions")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<PageResponse<List<TransactionResponse>>> getTransactions(Pageable pageable) {
        User user = userService.getByUsername(auth.getUsername());
        log.info("Request to get transactions for userId={}", user.getId());
        PageResponse<List<TransactionResponse>> transactions = tradingService.getTransactions(user.getId(), pageable);
        return new ResponseData<>(
                HttpStatus.OK.value(),
                Translator.toLocale("trading.transactions.success"),
                transactions
        );
    }

    @Operation(summary = "Get wallet balance", description = "API to get wallet balance")
    @GetMapping("/wallet")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<?> getWalletBalance() {
        User user = userService.getByUsername(auth.getUsername());
        log.info("Request to get wallet balance for userId={}", user.getId());
        Wallet wallet = tradingService.getWalletBalance(user.getId());
        WalletResponse response = new WalletResponse(wallet);
        return new ResponseData<>(
                HttpStatus.OK.value(),
                Translator.toLocale("trading.wallet.success"),
                response
        );
    }
}