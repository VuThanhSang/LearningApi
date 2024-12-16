package com.example.learning_api.service.core.Impl;

import com.example.learning_api.config.ModelMapperConfig;
import com.example.learning_api.dto.response.cart.CartResponse;
import com.example.learning_api.entity.sql.database.CartEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import com.example.learning_api.repository.database.CartRepository;
import com.example.learning_api.repository.database.ClassRoomRepository;
import com.example.learning_api.repository.database.UserRepository;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ICartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService implements ICartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ClassRoomRepository classroomRepository;
    private final ModelMapperService modelMapperService;
    @Override
    public void addToCart(CartEntity cartEntity) {
       try{
           if (cartEntity.getUserId()==null){
               throw new IllegalArgumentException("UserId is required");
           }
          if (cartEntity.getClassroomId()==null){
            throw new IllegalArgumentException("ClassroomId is required");
          }
           UserEntity userEntity = userRepository.findById(cartEntity.getUserId())
                   .orElseThrow(() -> new IllegalArgumentException("User not found"));
          if (userEntity==null){
              throw new IllegalArgumentException("User not found");
          }
           classroomRepository.findById(cartEntity.getClassroomId());
           CartEntity cart = new CartEntity();
           cart.setUserId(cartEntity.getUserId());
           cart.setClassroomId(cartEntity.getClassroomId());
           cart.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
           cart.setCreatedAt(String.valueOf(System.currentTimeMillis()));
           cartRepository.save(cart);


       }
         catch (Exception e){
             throw new IllegalArgumentException("Error while adding to cart");
         }
    }

    @Override
    public void removeFromCart(CartEntity cartEntity) {
        try{
            CartEntity cart = cartRepository.findById(cartEntity.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
            cartRepository.deleteById(cartEntity.getId());
        }
        catch (Exception e){
            throw new IllegalArgumentException("Error while removing from cart");
        }

    }

    @Override
    public void updateCart(CartEntity cartEntity) {

    }

    @Override
    public List<CartResponse> getCartByUserId(String userId) {
        try{
            List<CartEntity> cartEntities = cartRepository.findByUserId(userId);
            List<CartResponse> cartResponses = new ArrayList<>();
            for (CartEntity cartEntity: cartEntities){
                   CartResponse cartResponse = modelMapperService.mapClass(cartEntity, CartResponse.class);
                    cartResponse.setClassroom(classroomRepository.findById(cartEntity.getClassroomId())
                            .orElseThrow(() -> new IllegalArgumentException("Classroom not found")));
                    cartResponses.add(cartResponse);
            }
            return cartResponses;
        }
        catch (Exception e){
            throw new IllegalArgumentException("Error while getting cart");
        }
    }
}
