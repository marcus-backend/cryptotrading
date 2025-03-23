package com.marcus.service.impl;

import com.marcus.exception.BusinessException;
import com.marcus.model.auth.User;
import com.marcus.model.core.Asset;
import com.marcus.model.core.Coin;
import com.marcus.model.core.Order;
import com.marcus.model.core.OrderItem;
import com.marcus.model.core.Wallet;
import com.marcus.repository.core.OrderItemRepository;
import com.marcus.repository.core.OrderRepository;
import com.marcus.repository.core.WalletRepository;
import com.marcus.service.AssetService;
import com.marcus.service.OrderService;
import com.marcus.service.WalletService;
import com.marcus.util.OrderStatus;
import com.marcus.util.OrderType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

// Hints: Bid Price use for SELL order, Ask Price use for BUY order
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WalletService walletService;
    private final AssetService assetService;
    private final OrderItemRepository orderItemRepository;

    @Override
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {
        // Use askPrice for BUY, bidPrice for SELL
        BigDecimal price = orderType == OrderType.BUY
                ? orderItem.getCoin().getAskPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))
                : orderItem.getCoin().getBidPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        Order order = new Order();
        order.setUser(user);
        order.setOrderItem(orderItem);
        order.setOrderType(orderType);
        order.setPrice(price);
        order.setTimestamp(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found!"));
    }

    @Override
    public List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol) {
        return orderRepository.findByUserId(userId);
    }

    private OrderItem createOrderItem(Coin coin, double quantity, BigDecimal buyPrice, BigDecimal sellPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice.doubleValue());
        orderItem.setSellPrice(sellPrice.doubleValue());
        return orderItemRepository.save(orderItem);
    }

    @Transactional
    public Order buyAsset(Coin coin, double quantity, User user) throws BusinessException {
        if (quantity <= 0) {
            throw new BusinessException("Quantity should be greater than 0");
        }
        BigDecimal buyPrice = coin.getAskPrice(); // Use askPrice for BUY as per hint
        OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, BigDecimal.ZERO);
        Order order = createOrder(user, orderItem, OrderType.BUY);
        orderItem.setOrder(order);

        walletService.payOrderPayment(order, user);

        order.setStatus(OrderStatus.SUCCESS);
        order.setOrderType(OrderType.BUY);
        Order savedOrder = orderRepository.save(order);

        Asset oldAsset = assetService.findAssetByUserIdAndCoinId(
                order.getUser().getId(),
                order.getOrderItem().getCoin().getId()
        );

        if (oldAsset == null) {
            assetService.createAsset(user, orderItem.getCoin(), orderItem.getQuantity());
        } else {
            assetService.updateAsset(oldAsset.getId(), quantity);
        }
        return savedOrder;
    }

    @Transactional
    public Order sellAsset(Coin coin, double quantity, User user) throws BusinessException {
        if (quantity <= 0) {
            throw new BusinessException("Quantity should be greater than 0");
        }
        BigDecimal sellPrice = coin.getBidPrice(); // Use bidPrice for SELL as per hint
        Asset assetToSell = assetService.findAssetByUserIdAndCoinId(
                user.getId(),
                coin.getId()
        );
        if (assetToSell == null) {
            throw new BusinessException("Asset not found");
        }

        BigDecimal buyPrice = BigDecimal.valueOf(assetToSell.getBuyPrice());
        OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, sellPrice);
        Order order = createOrder(user, orderItem, OrderType.SELL);
        orderItem.setOrder(order);

        if (assetToSell.getQuantity() >= quantity) {
            order.setStatus(OrderStatus.SUCCESS);
            order.setOrderType(OrderType.SELL);
            Order savedOrder = orderRepository.save(order);

            walletService.payOrderPayment(order, user);

            Asset updatedAsset = assetService.updateAsset(
                    assetToSell.getId(), -quantity
            );

            if (updatedAsset.getQuantity() * coin.getBidPrice().doubleValue() <= 1) {
                assetService.deleteAsset(updatedAsset.getId());
            }
            return savedOrder;
        }
        throw new BusinessException("Insufficient quantity to sell");
    }


    @Override
    @Transactional
    public Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws BusinessException {
        if (orderType == OrderType.BUY) {
            return buyAsset(coin, quantity, user);
        } else if (orderType == OrderType.SELL) {
            return sellAsset(coin, quantity, user);
        }
        throw new BusinessException("Invalid order type");
    }
}
