package com.example.learning_api.dto.common.document;

import com.example.learning_api.enums.BlockType;
import lombok.Data;

@Data
public class ContentBlock {
    private BlockType type;
    private String content;
    public ContentBlock(BlockType type, String content) {
        this.type = type;
        this.content = content;
    }

}
