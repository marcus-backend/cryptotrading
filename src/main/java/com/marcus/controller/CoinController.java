package com.marcus.controller;

import com.marcus.dto.response.ResponseData;
import com.marcus.dto.response.ResponseError;
import com.marcus.model.auth.User;
import com.marcus.model.core.Coin;
import com.marcus.model.core.Wallet;
import com.marcus.model.core.Withdraw;
import com.marcus.repository.core.CoinRepository;
import com.marcus.service.CoinService;
import com.marcus.service.UserService;
import com.marcus.service.WalletService;
import com.marcus.service.WithdrawService;
import com.marcus.util.OrderType;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/coin")
@Validated
@Slf4j
@Tag(name = "Coin Controller")
@RequiredArgsConstructor
public class CoinController {
    private final CoinService coinService;
    private final CoinRepository coinRepository;

    @Operation(summary = "Get aggregated price", description = "API to get the latest best aggregated price for a given symbol")
    @GetMapping("/aggregated-price")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<?> getAggregatedPrice(@RequestParam String symbol, @RequestParam OrderType type) {
        log.info("Request get aggregated price for symbol={}", symbol);
        try {
            BigDecimal aggregatedPrice = coinService.getBestAggregatedPrice(symbol, type);
            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "Aggregated price retrieved successfully",
                    aggregatedPrice
            );
        } catch (Exception e) {
            log.info("Error message = {}", e.getMessage(), e.getCause());
            return new ResponseError(
                    HttpStatus.BAD_REQUEST.value(),
                    "Failed to retrieve aggregated price: " + e.getMessage()
            );
        }
    }

    @Operation(summary = "Get latest coin data", description = "API to get the latest coin data for a given symbol")
    @GetMapping("/latest/{symbol}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Coin> getLatestBestPrice(@PathVariable String symbol) {
        log.info("Request get latest coin data for symbol={}", symbol);
        return coinRepository.findBySymbol(symbol)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


}
