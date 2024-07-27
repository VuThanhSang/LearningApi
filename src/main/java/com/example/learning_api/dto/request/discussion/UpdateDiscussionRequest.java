package com.example.learning_api.dto.request.discussion;


import com.example.learning_api.dto.common.SourceUploadDto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateDiscussionRequest {
    private String id;
    private String title;
    private String content;
    private List<SourceUploadDto> sources;
    private String status;
}
