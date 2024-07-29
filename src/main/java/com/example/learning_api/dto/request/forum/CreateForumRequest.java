package com.example.learning_api.dto.request.forum;

import com.example.learning_api.dto.common.SourceUploadDto;
import lombok.Data;

import java.util.List;

@Data
public class CreateForumRequest {
    private String title;
    private String content;
    private String authorId;
    private List<SourceUploadDto> sources;
    private String status;
    private List<String> tags;
    private String role;
}
