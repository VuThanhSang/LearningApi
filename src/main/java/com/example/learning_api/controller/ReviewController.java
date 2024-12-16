package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.response.test.CreateTestResponse;
import com.example.learning_api.entity.sql.database.ReviewEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.core.IReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/review")
public class ReviewController {
    private final IReviewService reviewService;
    private final JwtService jwtService;
    @PostMapping("")
    public ResponseEntity<ResponseAPI<String>> createReview(@RequestBody ReviewEntity review,@RequestHeader("Authorization") String authorization){
        try{
            String token = authorization.substring(7);
            String userId = jwtService.extractUserId(token);
            review.setUserId(userId);
            reviewService.createReview(review);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create test successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            log.error("Error in creating review: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Error in creating review")
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseAPI<String>> updateReview(@PathVariable("id") String id, @RequestBody ReviewEntity review){
        try{
            reviewService.updateReview(id,review);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update review successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error in updating review: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Error in updating review")
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseAPI<String>> deleteReview(@PathVariable("id") String id){
        try{
            reviewService.deleteReview(id);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete review successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            log.error("Error in deleting review: ", e);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Error in deleting review")
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

}
