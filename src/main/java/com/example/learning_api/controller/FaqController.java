package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.comment.CreateCommentRequest;
import com.example.learning_api.dto.request.comment.UpdateCommentRequest;
import com.example.learning_api.dto.request.faq.CreateFaqRequest;
import com.example.learning_api.dto.request.faq.UpdateFaqRequest;
import com.example.learning_api.dto.response.comment.GetCommentByFaqResponse;
import com.example.learning_api.dto.response.faq.GetFaqDetailResponse;
import com.example.learning_api.dto.response.faq.GetFaqsResponse;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.core.IFaqCommentService;
import com.example.learning_api.service.core.IFaqService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.model.IComment;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(FAQ_BASE_PATH)
public class FaqController {
    private final IFaqService faqService;
    private final IFaqCommentService commentService;
    private final JwtService jwtService;
    @PostMapping(path = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> createFaq(@ModelAttribute @Valid CreateFaqRequest body, @RequestHeader("Authorization") String authorizationHeader) {
        try{
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String userId= jwtService.extractUserId(accessToken);
            body.setUserId(userId);
            faqService.createFaq(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create faq successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
    @PatchMapping(path = "/{faqId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> updateFaq(@ModelAttribute @Valid UpdateFaqRequest body, @PathVariable String faqId) {
        try{
            body.setId(faqId);
            faqService.updateFaq(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update faq successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/{faqId}")
    public ResponseEntity<ResponseAPI<String>> deleteFaq(@PathVariable String faqId) {
        try{
            faqService.deleteFaq(faqId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete faq successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @GetMapping(path = "")
    public ResponseEntity<ResponseAPI<GetFaqsResponse>> getFaqs(@RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                @RequestParam(name="size",required = false,defaultValue = "10") int size,
                                                                @RequestParam(name="search",required = false,defaultValue = "") String search,
                                                                @RequestParam(name="sort",required = false,defaultValue = "createdAt") String sort,
                                                                @RequestParam(name="order",required = false,defaultValue = "desc") String order) {
        try {
            if (search == null || search.isEmpty()) {
                search = "";
            }

            if (sort == null || sort.isEmpty()) {
                sort = "createdAt";
            }
            if (order == null || order.isEmpty()) {
                order = "desc";
            }

            GetFaqsResponse data = faqService.getFaqs(page - 1, size, search, sort, order);
            ResponseAPI<GetFaqsResponse> res = ResponseAPI.<GetFaqsResponse>builder()
                    .timestamp(new Date())
                    .data(data)
                    .message("Get faqs successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        } catch (Exception e) {
            ResponseAPI<GetFaqsResponse> res = ResponseAPI.<GetFaqsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/{faqId}/comments")
    public ResponseEntity<ResponseAPI<GetCommentByFaqResponse>> getCommentByFaqId(@PathVariable String faqId,
                                                                 @RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                 @RequestParam(name="size",required = false,defaultValue = "10") int size) {
        try{
            GetCommentByFaqResponse data = commentService.getCommentByFaqId( page-1, size,faqId);
            ResponseAPI<GetCommentByFaqResponse> res = ResponseAPI.<GetCommentByFaqResponse>builder()
                    .timestamp(new Date())
                    .data(data)
                    .message("Get comment by faq successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetCommentByFaqResponse> res = ResponseAPI.<GetCommentByFaqResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/{faqId}")
    public ResponseEntity<ResponseAPI<GetFaqDetailResponse>> getFaqDetail(@PathVariable String faqId) {
        try{
            GetFaqDetailResponse data = faqService.getFaqDetail(faqId);
            ResponseAPI<GetFaqDetailResponse> res = ResponseAPI.<GetFaqDetailResponse>builder()
                    .timestamp(new Date())
                    .data(data)
                    .message("Get faq detail successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetFaqDetailResponse> res = ResponseAPI.<GetFaqDetailResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/{faqId}/comment",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> createComment(@PathVariable String faqId,@ModelAttribute @Valid CreateCommentRequest body, @RequestHeader("Authorization") String authorizationHeader){
        try{
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String userId= jwtService.extractUserId(accessToken);
            body.setUserId(userId);
            body.setFaqId(faqId);
            commentService.createComment(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create comment successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @PatchMapping(path = "/comment/{commentId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> updateComment(@ModelAttribute @Valid UpdateCommentRequest body, @PathVariable String commentId) {
        try{
            body.setId(commentId);
            commentService.updateComment(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update comment successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @DeleteMapping(path = "/comment/{commentId}")
    public ResponseEntity<ResponseAPI<String>> deleteComment(@PathVariable String commentId) {
        try{
            commentService.deleteComment(commentId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete comment successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/comment/{commentId}/replies")
    public ResponseEntity<ResponseAPI<GetCommentByFaqResponse>> getRepliesByParentId(@PathVariable String commentId,
                                                                                     @RequestParam(name="page",required = false,defaultValue = "1") int page,
                                                                                     @RequestParam(name="size",required = false,defaultValue = "10") int size) {
        try{
            GetCommentByFaqResponse data = commentService.getRepliesByParentId( page-1, size,commentId);
            ResponseAPI<GetCommentByFaqResponse> res = ResponseAPI.<GetCommentByFaqResponse>builder()
                    .timestamp(new Date())
                    .data(data)
                    .message("Get replies by parent id successfully")
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetCommentByFaqResponse> res = ResponseAPI.<GetCommentByFaqResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

}
