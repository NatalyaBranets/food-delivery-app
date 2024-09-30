package com.foodhub.delivery_api.controller;

import com.foodhub.delivery_api.dto.order.CreateOrderRequestDTO;
import com.foodhub.delivery_api.dto.order.OrderDTO;
import com.foodhub.delivery_api.dto.order.OrdersDataDTO;
import com.foodhub.delivery_api.dto.order.UpdateOrderStatusRequestDTO;
import com.foodhub.delivery_api.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody @Valid CreateOrderRequestDTO request) {
        OrderDTO createdOrder = this.orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<OrdersDataDTO> getAllOrdersByUserId(@PathVariable("userId") Long userId,
                                                              @RequestParam(name = "page", defaultValue = "1") Integer page) {
        OrdersDataDTO ordersData = this.orderService.getAllOrdersByUserId(userId, page);
        return ResponseEntity.ok(ordersData);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable("id") UUID orderId,
                                                      @RequestBody @Valid UpdateOrderStatusRequestDTO request) {
        OrderDTO updatedOrder = this.orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable("id") UUID id) {
        this.orderService.deleteOrder(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{orderId}/item/{itemId}")
    public ResponseEntity<HttpStatus> deleteItemFromOrder(@PathVariable("orderId") UUID orderId,
                                                          @PathVariable("itemId") Long itemId) {
        this.orderService.deleteItemFromOrder(orderId, itemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
