package com.example.learning_api.dto.response.workspace;

import com.example.learning_api.entity.sql.database.WorkspaceMemberEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetWorkspaceMembersResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<WorkspaceMemberEntity> members;
}
