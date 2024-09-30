package com.foodhub.delivery_api.dto.order_item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodhub.delivery_api.dto.food.FoodDTO;
import com.foodhub.delivery_api.dto.order.OrderDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long id;
    private Integer quantity;
    private BigDecimal price;
    private FoodDTO food;
    @JsonIgnore
    private OrderDTO order;
}
