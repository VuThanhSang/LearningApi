package com.example.learning_api.dto.response.workspace;

import com.example.learning_api.entity.sql.database.WorkspaceEntity;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.enums.WorkspaceType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetWorkspacesResponse {
    private Integer totalPage;
    private Long totalElements;
    private List<WorkspaceEntity> workspaces;

}
