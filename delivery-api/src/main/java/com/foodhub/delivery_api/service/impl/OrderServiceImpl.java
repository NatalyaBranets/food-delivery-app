package com.foodhub.delivery_api.service.impl;

import com.foodhub.delivery_api.dto.order.CreateOrderRequestDTO;
import com.foodhub.delivery_api.dto.order.OrderDTO;
import com.foodhub.delivery_api.dto.order.OrdersDataDTO;
import com.foodhub.delivery_api.dto.order.UpdateOrderStatusRequestDTO;
import com.foodhub.delivery_api.enums.OrderStatus;
import com.foodhub.delivery_api.exception.custom_exceptions.ResourceNotFoundException;
import com.foodhub.delivery_api.mapper.OrderMapper;
import com.foodhub.delivery_api.model.*;
import com.foodhub.delivery_api.repository.FoodRepository;
import com.foodhub.delivery_api.repository.OrderItemRepository;
import com.foodhub.delivery_api.repository.OrderRepository;
import com.foodhub.delivery_api.repository.UserRepository;
import com.foodhub.delivery_api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private OrderMapper orderMapper;

    @Override
    public OrderDTO placeOrder(CreateOrderRequestDTO request) {
        User user = this.userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id = %s does not exist", request.userId())));

        Order order = new Order();
        order.setUser(user);
        order.setOrderTime(LocalDateTime.now());
        order.setPaid(true);
        order.setStatus(OrderStatus.PROCESSING);

        request.items().forEach(selectedItem -> {
            Food food = this.foodRepository.findById(selectedItem.foodId())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Product with id = %s does not exist", selectedItem.foodId())));
            OrderItem item = new OrderItem();
            item.setFood(food);
            item.setQuantity(selectedItem.quantity());
            item.setPrice(selectedItem.price());

            order.addOrderItem(item);
        });

        // save
        Order savedOrder = this.orderRepository.save(order);

        return this.orderMapper.convertToOrderDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdersDataDTO getAllOrdersByUserId(Long userId, Integer page) {
        boolean isExistsUserById = this.userRepository.existsById(userId);
        if (!isExistsUserById) {
            throw new ResourceNotFoundException(String.format("User with id = %s does not exist", userId));
        }

        int pageNo = page < 1 ? 1 : page - 1;
        PageRequest pageRequest = PageRequest.of(pageNo, 10, Sort.Direction.DESC, "orderTime");
        Page<Order> ordersPage = this.orderRepository.findByUserId(userId, pageRequest);

        List<OrderDTO> orderDTOs = ordersPage.getContent()
                .stream()
                .map(order -> this.orderMapper.convertToOrderDTO(order))
                .toList();

        return new OrdersDataDTO(orderDTOs, ordersPage);
    }

    @Override
    public OrderDTO updateOrderStatus(UUID orderId, UpdateOrderStatusRequestDTO request) {
        Order orderToUpdate = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Order with id = %s does not exist", orderId)));

        orderToUpdate.setStatus(request.status());

        // save
        Order savedOrder = this.orderRepository.save(orderToUpdate);

        return this.orderMapper.convertToOrderDTO(savedOrder);
    }

    @Override
    public void deleteOrder(UUID id) {
        boolean isExistsById = this.orderRepository.existsById(id);
        if (!isExistsById) {
            throw new ResourceNotFoundException(String.format("Order with id %s not found", id));
        }
        this.orderRepository.deleteById(id);
    }

    @Override
    public void deleteItemFromOrder(UUID orderId, Long itemId) {
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Order with id %s not found", orderId)));

        List<OrderItem> orderItems = order.getOrderItems();
        Optional<OrderItem> itemToDelete = orderItems.stream()
                .filter(item -> Objects.equals(item.getId(), itemId))
                .findFirst();
        itemToDelete.ifPresent(order::removeOrderItem);

        // save after update
        this.orderRepository.save(order);
    }
}
