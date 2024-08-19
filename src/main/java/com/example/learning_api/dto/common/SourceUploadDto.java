package com.example.learning_api.dto.common;

import com.example.learning_api.enums.FaqSourceType;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SourceUploadDto {
        private FaqSourceType type;
        private MultipartFile path;
}
