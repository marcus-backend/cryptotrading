package com.marcus.service.impl;

import com.marcus.dto.response.BinanceCoin;
import com.marcus.dto.response.HuobiCoin;
import com.marcus.dto.response.HuobiResponse;
import com.marcus.exception.BusinessException;
import com.marcus.model.core.Coin;
import com.marcus.repository.core.CoinRepository;
import com.marcus.service.CoinService;
import com.marcus.util.OrderType;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoinServiceImpl implements CoinService {
    private final CoinRepository coinRepository;
    private final RestTemplate restTemplate;
    private static final String BINANCE_API_URL = "https://api.binance.com/api/v3/ticker/bookTicker";
    private static final String HUOBI_API_URL = "https://api.huobi.pro/market/tickers";
    private static final List<String> SUPPORTED_PAIRS = Arrays.asList("ETHUSDT", "BTCUSDT");

    @Scheduled(cron = "*/10 * * * * *") // Every 10 seconds
    @Transactional
    public void fetchAndSaveCoinData() {
        Map<String, List<Coin>> priceMap = new HashMap<>();

        CompletableFuture<Void> binanceFuture = CompletableFuture.runAsync(() -> fetchBinanceData(priceMap));
        CompletableFuture<Void> huobiFuture = CompletableFuture.runAsync(() -> fetchHuobiData(priceMap));

        CompletableFuture.allOf(binanceFuture, huobiFuture).join();

        SUPPORTED_PAIRS.forEach(symbol -> {
            List<Coin> prices = priceMap.getOrDefault(symbol, Collections.emptyList());
            if (!prices.isEmpty()) {
                Coin bestCoin = determineBestPrice(prices);
                updateOrSaveCoin(bestCoin);
            }
        });

        log.info("Coin prices updated at {}", java.time.LocalDateTime.now());
    }

    @Async
    public void fetchBinanceData(Map<String, List<Coin>> priceMap) {
        try {
            BinanceCoin[] coins = restTemplate.getForObject(BINANCE_API_URL, BinanceCoin[].class);
            if (coins != null) {
                for (BinanceCoin coin : coins) {
                    if (SUPPORTED_PAIRS.contains(coin.getSymbol().toUpperCase())) {
                        Coin newCoin = Coin.builder()
                                .symbol(coin.getSymbol().toUpperCase())
                                .bidPrice(coin.getBidPrice())
                                .bidQty(coin.getBidQty())
                                .askPrice(coin.getAskPrice())
                                .askQty(coin.getAskQty())
                                .build();
                        priceMap.computeIfAbsent(newCoin.getSymbol(), k -> new ArrayList<>()).add(newCoin);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error fetching Binance data: {}", e.getMessage());
        }
    }

    @Async
    public void fetchHuobiData(Map<String, List<Coin>> priceMap) {
        try {
            HuobiResponse response = restTemplate.getForObject(HUOBI_API_URL, HuobiResponse.class);
            if (response != null && response.getData() != null) {
                for (HuobiResponse.HuobiCoin coin : response.getData()) {
                    String symbol = coin.getSymbol().toUpperCase();
                    if (SUPPORTED_PAIRS.contains(symbol)) {
                        Coin newCoin = Coin.builder()
                                .symbol(symbol)
                                .bidPrice(coin.getBid())
                                .bidQty(coin.getBidSize())
                                .askPrice(coin.getAsk())
                                .askQty(coin.getAskSize())
                                .openPrice(coin.getOpen())
                                .highPrice(coin.getHigh())
                                .lowPrice(coin.getLow())
                                .closePrice(coin.getClose())
                                .amount(coin.getAmount())
                                .volume(coin.getVol())
                                .count(coin.getCount())
                                .build();
                        priceMap.computeIfAbsent(symbol, k -> new ArrayList<>()).add(newCoin);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error fetching Huobi data: {}", e.getMessage());
        }
    }

    @Transactional
    public void updateOrSaveCoin(Coin coin) {
        int retries = 3;
        while (retries > 0) {
            try {
                Coin existingCoin = coinRepository.findBySymbol(coin.getSymbol()).orElse(null);
                if (existingCoin != null) {
                    existingCoin.setBidPrice(coin.getBidPrice());
                    existingCoin.setBidQty(coin.getBidQty());
                    existingCoin.setAskPrice(coin.getAskPrice());
                    existingCoin.setAskQty(coin.getAskQty());
                    existingCoin.setOpenPrice(coin.getOpenPrice());
                    existingCoin.setHighPrice(coin.getHighPrice());
                    existingCoin.setLowPrice(coin.getLowPrice());
                    existingCoin.setClosePrice(coin.getClosePrice());
                    existingCoin.setAmount(coin.getAmount());
                    existingCoin.setVolume(coin.getVolume());
                    existingCoin.setCount(coin.getCount());
                    coinRepository.save(existingCoin);
                } else {
                    coinRepository.save(coin);
                }
                break;
            } catch (Exception e) {
                retries--;
                if (retries == 0) {
                    log.error("Failed to update coin data for symbol: {}", coin.getSymbol(), e);
                }
            }
        }
    }

    private Coin determineBestPrice(List<Coin> prices) {
        BigDecimal bestBidPrice = prices.stream()
                .map(Coin::getBidPrice)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        BigDecimal bestAskPrice = prices.stream()
                .map(Coin::getAskPrice)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        Coin bestCoin = prices.get(0);
        bestCoin.setBidPrice(bestBidPrice);
        bestCoin.setAskPrice(bestAskPrice);
        return bestCoin;
    }

    @Override
    public Coin findById(Long coinId) {
        return coinRepository.findById(coinId)
                .orElseThrow(() -> new RuntimeException("Coin not found with ID: " + coinId));
    }

    @Override
    public BigDecimal getBestAggregatedPrice(String symbol, OrderType type) {
        Coin coin = coinRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("No price data for symbol: " + symbol));
        return OrderType.BUY.equals(type) ? coin.getAskPrice() : coin.getBidPrice();
    }
}
