package com.foodhub.delivery_api.mapper;

import com.foodhub.delivery_api.dto.food.FoodDTO;
import com.foodhub.delivery_api.dto.order.OrderDTO;
import com.foodhub.delivery_api.dto.order_item.OrderItemDTO;
import com.foodhub.delivery_api.enums.OrderStatus;
import com.foodhub.delivery_api.model.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class OrderMapper {

    public OrderDTO convertToOrderDTO(Order order) {
        UUID id = order.getId();
        LocalDateTime orderTime = order.getOrderTime();
        boolean isPaid = order.isPaid();
        OrderStatus status = order.getStatus();
        List<OrderItemDTO> orderItems = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemDTO dto = new OrderItemDTO();
                    dto.setId(item.getId());
                    dto.setQuantity(item.getQuantity());
                    dto.setFood(new FoodDTO(item.getFood()));
                    dto.setPrice(item.getPrice());
                    return dto;
                })
                .toList();

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(id);
        orderDTO.setStatus(status);
        orderDTO.setOrderItems(orderItems);
        orderDTO.setPaid(isPaid);
        orderDTO.setOrderTime(orderTime);

        return orderDTO;
    }
}
