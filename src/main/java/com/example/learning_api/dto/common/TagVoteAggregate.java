package com.example.learning_api.dto.common;

import lombok.Data;

@Data
public class TagVoteAggregate {
    private String _id;  // tag name
    private int voteCount;
}