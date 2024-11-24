package com.example.learning_api.dto.response.forum;

import com.example.learning_api.entity.sql.database.StudentEntity;
import com.example.learning_api.entity.sql.database.TeacherEntity;
import com.example.learning_api.entity.sql.database.UserEntity;
import lombok.Data;
import org.apache.catalina.User;

import java.util.List;

@Data
public class GetVotesResponse {
    private int totalElement;
    private int totalUpvote;
    private int totalDownvote;
    private List<UserEntity> upVotes;
    private List<UserEntity> downVotes;

}
