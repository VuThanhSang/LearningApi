package com.example.learning_api.service.core;

import com.example.learning_api.entity.sql.database.ReviewEntity;

public interface IReviewService {
    void createReview(ReviewEntity review);
    void updateReview(String id, ReviewEntity review);
    void deleteReview(String id);

}
