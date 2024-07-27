package com.example.learning_api.dto.request.discussion;

import com.example.learning_api.dto.common.SourceUploadDto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateDiscussionCommentRequest {
    private String id;
    private String content;
    private int upvote;
    private int downvote;
    private int replyCount;
    private String status;
    private List<SourceUploadDto> sources;

}
