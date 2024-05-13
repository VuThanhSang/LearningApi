package com.example.learning_api.controller;


import com.example.learning_api.dto.request.comment.CreateCommentRequest;
import com.example.learning_api.dto.request.comment.UpdateCommentRequest;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.learning_api.constant.RouterConstant.COMMENT_BASE_PATH;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(COMMENT_BASE_PATH)
public class CommentController {
    private final ICommentService commentService;
    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<String>> createComment(@RequestBody @Valid CreateCommentRequest body) {
        try{
            commentService.createComment(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Create comment successfully")
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }


    @PatchMapping(path = "/{commentId}")
    public ResponseEntity<ResponseAPI<String>> updateComment(@RequestBody @Valid UpdateCommentRequest body, @PathVariable String commentId) {
        try{
            body.setId(commentId);
            commentService.updateComment(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update comment successfully")
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @DeleteMapping(path = "/{commentId}")
    public ResponseEntity<ResponseAPI<String>> deleteComment(@PathVariable String commentId) {
        try{
            commentService.deleteComment(commentId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete comment successfully")
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }


}
