package com.example.learning_api.service.core;

import com.example.learning_api.dto.response.cart.CartResponse;
import com.example.learning_api.entity.sql.database.CartEntity;

import java.util.List;

public interface ICartService {
    void addToCart(CartEntity cartEntity);
    void removeFromCart(CartEntity cartEntity);
    void updateCart(CartEntity cartEntity);
    List<CartResponse> getCartByUserId(String userId);
}
