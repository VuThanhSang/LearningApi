package com.example.learning_api.dto.common;

import lombok.Data;

@Data
public class FileDto {
    private String filePath;
    private String fileName;
    private String fileType;
    private String fileSize;
    private String fileExtension;
}
