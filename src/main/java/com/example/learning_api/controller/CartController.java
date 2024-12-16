package com.example.learning_api.controller;

import com.example.learning_api.dto.response.cart.CartResponse;
import com.example.learning_api.entity.sql.database.CartEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.ICartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/cart")
public class CartController {
    private final ICartService cartService;

    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<String>> addToCart(@RequestBody CartEntity cartEntity) {
        try {
            cartService.addToCart(cartEntity);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Add to cart successfully")
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ResponseAPI<String>> removeFromCart(@PathVariable String id) {
        try {
            CartEntity cartEntity = new CartEntity();
            cartEntity.setId(id);
            cartService.removeFromCart(cartEntity);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Remove from cart successfully")
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }


    @GetMapping(path = "/user/{userId}")
    public ResponseEntity<ResponseAPI<List<CartResponse>>> getCartByUserId(@PathVariable String userId) {
        try {
            List<CartResponse> data= cartService.getCartByUserId(userId);
            ResponseAPI<List<CartResponse>> res = ResponseAPI.<List<CartResponse>>builder()
                    .message("Get cart by user id successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<List<CartResponse>> res = ResponseAPI.<List<CartResponse>>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }
}
