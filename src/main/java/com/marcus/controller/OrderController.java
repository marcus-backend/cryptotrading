package com.marcus.controller;

import com.marcus.dto.request.CreateOrderRequest;
import com.marcus.model.auth.User;
import com.marcus.model.core.Coin;
import com.marcus.model.core.Order;
import com.marcus.service.CoinService;
import com.marcus.service.OrderService;
import com.marcus.service.UserService;
import com.marcus.util.OrderType;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
@Validated
@Slf4j
@Tag(name = "Wallet Controller")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final CoinService coinService;


    @PostMapping("/{userId}/pay")
    public ResponseEntity<Order> payOrderPayment(
            @PathVariable long userId,
            @RequestBody CreateOrderRequest req
    ) {
        User user = userService.getUserById(userId);
        Coin coin = coinService.findById(req.getCoinId());
        Order order = orderService.processOrder(coin, req.getQuantity(), req.getOrderType(), user);
        return ResponseEntity.ok(order);

    }

    @GetMapping("/{userId}/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable long userId,
            @PathVariable long orderId
    ) {
        User user = userService.getUserById(userId);
        Order order = orderService.getOrderById(orderId);
        if (order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok(order);

        }
        return null;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Order>> getAllOrders(
            @PathVariable long userId,
            @PathVariable(required = false) OrderType order_type,
            @PathVariable(required = false) String asset_symbol
    ) {
        User user = userService.getUserById(userId);
        List<Order> userOrders = orderService.getAllOrdersOfUser(userId, order_type, asset_symbol);
        return ResponseEntity.ok(userOrders);
    }

}
