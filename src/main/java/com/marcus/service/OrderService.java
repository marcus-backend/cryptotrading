package com.marcus.service;

import com.marcus.exception.BusinessException;
import com.marcus.model.auth.User;
import com.marcus.model.core.Coin;
import com.marcus.model.core.Order;
import com.marcus.model.core.OrderItem;
import com.marcus.util.OrderType;

import java.util.List;

public interface OrderService {
    Order getOrderById(Long orderId);
    List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol);
    Order buyAsset(Coin coin, double quantity, User user) throws BusinessException;
    Order sellAsset(Coin coin, double quantity, User user) throws BusinessException;
    Order create(User user, OrderItem orderItem, OrderType orderType);
    Order process(Coin coin, double quantity, OrderType orderType, User user) throws BusinessException;
}
