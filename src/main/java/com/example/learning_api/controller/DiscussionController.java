package com.example.learning_api.controller;

import com.example.learning_api.dto.request.discussion.CreateDiscussionCommentRequest;
import com.example.learning_api.dto.request.discussion.CreateDiscussionRequest;
import com.example.learning_api.dto.request.discussion.UpdateDiscussionCommentRequest;
import com.example.learning_api.dto.request.discussion.UpdateDiscussionRequest;
import com.example.learning_api.dto.response.answer.CreateAnswerResponse;
import com.example.learning_api.dto.response.discussion.GetDiscussionCommentResponse;
import com.example.learning_api.dto.response.discussion.GetDiscussionDetailResponse;
import com.example.learning_api.dto.response.discussion.GetDiscussionsResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IDiscussionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/discussion")
public class DiscussionController {
    private final IDiscussionService discussionService;

    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> createDiscussion(@ModelAttribute @Valid CreateDiscussionRequest body) {
        try{
            discussionService.createDiscussion(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Create discussion successfully")
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
    @PatchMapping(path = "/{discussionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> updateDiscussion(@ModelAttribute @Valid UpdateDiscussionRequest body, @PathVariable String discussionId) {
        try{
            body.setId(discussionId);
            discussionService.updateDiscussion(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update discussion successfully")
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

    @DeleteMapping(path = "/{discussionId}")
    public ResponseEntity<ResponseAPI<String>> deleteDiscussion(@PathVariable String discussionId) {
        try{
            discussionService.deleteDiscussion(discussionId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete discussion successfully")
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

    @PostMapping(path = "/{discussionId}/upvote")
    public ResponseEntity<ResponseAPI<String>> upvoteDiscussion(@PathVariable String discussionId) {
        try{
            discussionService.upvoteDiscussion(discussionId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Upvote discussion successfully")
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
    @PostMapping(path = "/{discussionId}/downvote")
    public ResponseEntity<ResponseAPI<String>> downvoteDiscussion(@PathVariable String discussionId) {
        try{
            discussionService.downvoteDiscussion(discussionId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Downvote discussion successfully")
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

    @GetMapping(path = "")
    public ResponseEntity<ResponseAPI<GetDiscussionsResponse>> getDiscussion(
            @RequestParam(name="name",required = false,defaultValue = "") String search,
            @RequestParam(name="page",required = false,defaultValue = "1") int page,
            @RequestParam(name="size",required = false,defaultValue = "10") int size
    ) {
        try{
            GetDiscussionsResponse data = discussionService.getDiscussions(page-1, size, search);
            ResponseAPI<GetDiscussionsResponse> res = ResponseAPI.<GetDiscussionsResponse>builder()
                    .message("Get discussion successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<GetDiscussionsResponse> res = ResponseAPI.<GetDiscussionsResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }
    @GetMapping(path = "/author/{authorId}")
    public ResponseEntity<ResponseAPI<GetDiscussionsResponse>> getDiscussionByAuthor(@PathVariable String authorId,
                                                                                     @RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                                     @RequestParam(name="size",required = false,defaultValue = "10") int size) {
        try{
            GetDiscussionsResponse data = discussionService.getDiscussionByAuthor(authorId, page-1, size);
            ResponseAPI<GetDiscussionsResponse> res = ResponseAPI.<GetDiscussionsResponse>builder()
                    .message("Get discussion by author successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<GetDiscussionsResponse> res = ResponseAPI.<GetDiscussionsResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/tag")
    public ResponseEntity<ResponseAPI<GetDiscussionsResponse>> getDiscussionByTag(@RequestParam(name="search",required = false,defaultValue = "") String tag,
                                                                                  @RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                                  @RequestParam(name="size",required = false,defaultValue = "10") int size) {
        try{
            GetDiscussionsResponse data = discussionService.getDiscussionByTag(tag, page-1, size);
            ResponseAPI<GetDiscussionsResponse> res = ResponseAPI.<GetDiscussionsResponse>builder()
                    .message("Get discussion by tag successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<GetDiscussionsResponse> res = ResponseAPI.<GetDiscussionsResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }
    @GetMapping(path = "/{discussionId}")
    public ResponseEntity<ResponseAPI<GetDiscussionDetailResponse>> getDiscussionDetail(@PathVariable String discussionId) {
        try{
            GetDiscussionDetailResponse data = discussionService.getDiscussionDetail(discussionId);
            ResponseAPI<GetDiscussionDetailResponse> res = ResponseAPI.<GetDiscussionDetailResponse>builder()
                    .message("Get discussion detail successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<GetDiscussionDetailResponse> res = ResponseAPI.<GetDiscussionDetailResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }



    @PostMapping(path= "/comment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> createDiscussionComment(@ModelAttribute @Valid CreateDiscussionCommentRequest body) {
        try{
            discussionService.createDiscussionComment(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Create discussion comment successfully")
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

    @PatchMapping(path = "/comment/{commentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> updateDiscussionComment(@ModelAttribute @Valid UpdateDiscussionCommentRequest body, @PathVariable String commentId) {
        try{
            body.setId(commentId);
            discussionService.updateDiscussionComment(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update discussion comment successfully")
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

    @DeleteMapping(path = "/comment/{commentId}")
    public ResponseEntity<ResponseAPI<String>> deleteDiscussionComment(@PathVariable String commentId) {
        try{
            discussionService.deleteDiscussionComment(commentId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete discussion comment successfully")
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

    @GetMapping(path = "/comment/reply/{parentId}")
    public ResponseEntity<ResponseAPI<GetDiscussionCommentResponse>> getReplyComments(@PathVariable String parentId,
                                                                                      @RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                                      @RequestParam(name="size",required = false,defaultValue = "10") int size) {
        try{
            GetDiscussionCommentResponse data = discussionService.getReplyComments(parentId, page-1, size);
            ResponseAPI<GetDiscussionCommentResponse> res = ResponseAPI.<GetDiscussionCommentResponse>builder()
                    .message("Get reply comments successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<GetDiscussionCommentResponse> res = ResponseAPI.<GetDiscussionCommentResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }


}
