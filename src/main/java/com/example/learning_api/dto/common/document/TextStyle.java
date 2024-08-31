package com.example.learning_api.dto.common.document;

import lombok.Data;

@Data
public class TextStyle {
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private boolean strikethrough;
    private int fontSize;
    private String fontColor;
    private String highlightColor;

    // Getters and setters
}