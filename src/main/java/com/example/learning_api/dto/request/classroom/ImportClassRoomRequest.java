package com.example.learning_api.dto.request.classroom;

import com.example.learning_api.enums.ImportType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImportClassRoomRequest {
    private String text;
    private ImportType type;
    private MultipartFile file;
}
