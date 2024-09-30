package com.foodhub.delivery_api.service.impl;


import com.foodhub.delivery_api.TestBeansConfiguration;
import com.foodhub.delivery_api.dto.order.CreateOrderRequestDTO;
import com.foodhub.delivery_api.dto.order.OrderDTO;
import com.foodhub.delivery_api.dto.order_item.OrderItemRequestDTO;
import com.foodhub.delivery_api.mapper.OrderMapper;
import com.foodhub.delivery_api.model.Food;
import com.foodhub.delivery_api.model.Order;
import com.foodhub.delivery_api.model.OrderItem;
import com.foodhub.delivery_api.model.User;
import com.foodhub.delivery_api.repository.FoodRepository;
import com.foodhub.delivery_api.repository.OrderRepository;
import com.foodhub.delivery_api.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(classes = TestBeansConfiguration.class)
@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private FoodRepository foodRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderMapper orderMapper;

    @Test
    public void testPlaceOrder() {
        // prepare test data
        Long userId = 1L;
        Long foodId = 1L;
        Long orderItemId = 1L;
        int quantity = 4;
        BigDecimal price = BigDecimal.valueOf(1.0);
        OrderItemRequestDTO item1 = new OrderItemRequestDTO(orderItemId, quantity, price, foodId);
        List<OrderItemRequestDTO> items = Collections.singletonList(item1);
        CreateOrderRequestDTO request = new CreateOrderRequestDTO(items, userId);

        User user = new User();
        user.setId(userId);
        when(this.userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        Food food = new Food();
        food.setId(foodId);
        when(this.foodRepository.findById(foodId)).thenReturn(Optional.of(food));

        Order order = new Order();
        when(this.orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO orderDTO = new OrderDTO();
        when(this.orderMapper.convertToOrderDTO(order)).thenReturn(orderDTO);

        // act
        OrderDTO actual = this.orderService.placeOrder(request);

        // assert
        Assertions.assertNotNull(actual);
        verify(this.userRepository, times(1)).findById(userId);
        verify(this.foodRepository, times(1)).findById(foodId);
        verify(this.orderRepository, times(1)).save(any(Order.class));
        verify(this.orderMapper, times(1)).convertToOrderDTO(order);
        verifyNoMoreInteractions(this.userRepository, this.foodRepository, this.foodRepository, this.orderRepository, this.orderMapper);
    }

    @Test
    public void testDeleteOrder() {
        // prepare test data
        UUID id = UUID.fromString("50c3a35d-2c40-4987-a9dd-7aaa987a79b4");

        when(this.orderRepository.existsById(id)).thenReturn(true);

        doNothing().when(this.orderRepository).deleteById(id);

        // act
        this.orderService.deleteOrder(id);

        // assert
        verify(this.orderRepository, times(1)).existsById(id);
        verify(this.orderRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(this.orderRepository);
    }

    @Test
    public void testDeleteItemFromOrder() {
        // prepare test data
        UUID id = UUID.fromString("50c3a35d-2c40-4987-a9dd-7aaa987a79b4");
        Long itemId = 1L;

        OrderItem orderItem = new OrderItem();
        orderItem.setId(itemId);
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        Order order = new Order();
        order.setId(id);
        order.setOrderItems(orderItems);
        when(this.orderRepository.findById(id)).thenReturn(Optional.of(order));

        when(this.orderRepository.save(order)).thenReturn(order);

        // act
        this.orderService.deleteItemFromOrder(id, itemId);

        // assert
        verify(this.orderRepository, times(1)).findById(id);
        verify(this.orderRepository, times(1)).save(order);
        verifyNoMoreInteractions(this.orderRepository);
    }
}
