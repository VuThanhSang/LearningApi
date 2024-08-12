package com.example.learning_api.dto.common;

import lombok.Data;

@Data
public class MessageDto{
    private String to;
    private String subject;
    private String content;
    private String toName;

}
