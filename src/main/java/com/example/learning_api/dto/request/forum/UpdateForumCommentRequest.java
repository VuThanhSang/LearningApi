package com.example.learning_api.dto.request.forum;

import com.example.learning_api.dto.common.SourceUploadDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateForumCommentRequest {
    private String id;
    private String content;
    private int replyCount;
    private String status;
    private List<SourceUploadDto> sources;

}
