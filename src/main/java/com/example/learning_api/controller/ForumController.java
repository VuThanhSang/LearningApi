package com.example.learning_api.controller;

import com.example.learning_api.dto.request.forum.*;
import com.example.learning_api.dto.response.forum.GetForumCommentResponse;
import com.example.learning_api.dto.response.forum.GetForumDetailResponse;
import com.example.learning_api.dto.response.forum.GetForumsResponse;
import com.example.learning_api.dto.response.forum.GetVotesResponse;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TeacherRepository;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.core.IForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/forum")
public class ForumController {
    private final IForumService forumService;
    private final JwtService jwtService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    private String extractUserId(String authorizationHeader) throws Exception {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        return jwtService.extractUserId(accessToken);
    }

    private String extractRole(String authorizationHeader) throws Exception {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        return jwtService.extractRole(accessToken);
    }

    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> createForum(@ModelAttribute @Valid CreateForumRequest body, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            String authorId = "";
            if (role.equals("USER")) {
                authorId = studentRepository.findByUserId(userId).getId();
            } else if (role.equals("TEACHER")) {
                authorId = teacherRepository.findByUserId(userId).getId();
            } else {
                throw new Exception("Role not found");
            }
            body.setAuthorId(authorId);
            body.setRole(role);
            forumService.createForum(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Create forum successfully")
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PatchMapping(path = "/{forumId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> updateForum(@ModelAttribute @Valid UpdateForumRequest body, @PathVariable String forumId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            body.setId(forumId);
            forumService.updateForum(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update forum successfully")
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @DeleteMapping(path = "/{forumId}")
    public ResponseEntity<ResponseAPI<String>> deleteForum(@PathVariable String forumId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            forumService.deleteForum(forumId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete forum successfully")
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping(path = "/vote")
    public ResponseEntity<ResponseAPI<String>> voteForum(@RequestBody @Valid VoteRequest body, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role =extractRole(authorizationHeader);
            String authorId = "";
            if (role.equals("USER")) {
                authorId = studentRepository.findByUserId(userId).getId();
            } else if (role.equals("TEACHER")) {
                authorId = teacherRepository.findByUserId(userId).getId();
            }
            body.setAuthorId(authorId);
            body.setRole(RoleEnum.valueOf(role));
            forumService.voteForum(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Vote forum successfully")
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "")
    public ResponseEntity<ResponseAPI<GetForumsResponse>> getForum(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "desc") String sortOrder,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            GetForumsResponse data = forumService.getForums(page - 1, size, search, sortOrder);
            ResponseAPI<GetForumsResponse> res = ResponseAPI.<GetForumsResponse>builder()
                    .message("Get forum successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<GetForumsResponse> res = ResponseAPI.<GetForumsResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/author/{authorId}")
    public ResponseEntity<ResponseAPI<GetForumsResponse>> getForumByAuthor(@PathVariable String authorId,
                                                                           @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                           @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                                           @RequestParam(name = "search", required = false, defaultValue = "") String search,
                                                                           @RequestParam(name = "sortOrder", required = false, defaultValue = "desc") String sortOrder,
                                                                           @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            GetForumsResponse data = forumService.getForumByAuthor(authorId, page - 1, size, search, sortOrder);
            ResponseAPI<GetForumsResponse> res = ResponseAPI.<GetForumsResponse>builder()
                    .message("Get forum by author successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<GetForumsResponse> res = ResponseAPI.<GetForumsResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/tag")
    public ResponseEntity<ResponseAPI<GetForumsResponse>> getForumByTag(@RequestParam List<String> tags,
                                                                        @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                        @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                                        @RequestParam(name = "search", required = false, defaultValue = "") String search,
                                                                        @RequestParam(name = "sortOrder", required = false, defaultValue = "desc") String sortOrder,
                                                                        @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            GetForumsResponse data = forumService.getForumByTag(tags, page - 1, size, search, sortOrder);
            ResponseAPI<GetForumsResponse> res = ResponseAPI.<GetForumsResponse>builder()
                    .message("Get forum by tag successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<GetForumsResponse> res = ResponseAPI.<GetForumsResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/class/{classId}")
    public ResponseEntity<ResponseAPI<GetForumsResponse>> getForumByClass(@PathVariable String classId,
                                                                          @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                          @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                                          @RequestParam(name = "search", required = false, defaultValue = "") String search,
                                                                          @RequestParam(name = "sortOrder", required = false, defaultValue = "desc") String sortOrder,
                                                                          @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            GetForumsResponse data = forumService.getForumByClass(classId, page - 1, size, search, sortOrder);
            ResponseAPI<GetForumsResponse> res = ResponseAPI.<GetForumsResponse>builder()
                    .message("Get forum by class successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<GetForumsResponse> res = ResponseAPI.<GetForumsResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/{forumId}")
    public ResponseEntity<ResponseAPI<GetForumDetailResponse>> getForumDetail(@PathVariable String forumId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            GetForumDetailResponse data = forumService.getForumDetail(forumId);
            ResponseAPI<GetForumDetailResponse> res = ResponseAPI.<GetForumDetailResponse>builder()
                    .message("Get forum detail successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<GetForumDetailResponse> res = ResponseAPI.<GetForumDetailResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping(path = "/comment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> createForumComment(@ModelAttribute @Valid CreateForumCommentRequest body, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            String authorId = "";
            if (role.equals("USER")) {
                authorId = studentRepository.findByUserId(userId).getId();
            } else if (role.equals("TEACHER")) {
                authorId = teacherRepository.findByUserId(userId).getId();
            } else {
                throw new Exception("Role not found");
            }
            body.setAuthorId(authorId);
            body.setRole(role);
            forumService.createForumComment(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Create forum comment successfully")
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PatchMapping(path = "/comment/{commentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> updateForumComment(@ModelAttribute @Valid UpdateForumCommentRequest body, @PathVariable String commentId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            body.setId(commentId);
            forumService.updateForumComment(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update forum comment successfully")
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @DeleteMapping(path = "/comment/{commentId}")
    public ResponseEntity<ResponseAPI<String>> deleteForumComment(@PathVariable String commentId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            forumService.deleteForumComment(commentId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete forum comment successfully")
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/{forumId}/comment")
    public ResponseEntity<ResponseAPI<GetForumCommentResponse>> getForumComments(@PathVariable String forumId,
                                                                                 @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                                 @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                                                 @RequestParam(name = "sortOrder", required = false, defaultValue = "desc") String sortOrder,
                                                                                 @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            GetForumCommentResponse data = forumService.getForumComments(forumId, page - 1, size, sortOrder);
            ResponseAPI<GetForumCommentResponse> res = ResponseAPI.<GetForumCommentResponse>builder()
                    .message("Get forum comments successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<GetForumCommentResponse> res = ResponseAPI.<GetForumCommentResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/comment/reply/{parentId}")
    public ResponseEntity<ResponseAPI<GetForumCommentResponse>> getReplyComments(@PathVariable String parentId,
                                                                                 @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                                                 @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                                                 @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            GetForumCommentResponse data = forumService.getReplyComments(parentId, page - 1, size);
            ResponseAPI<GetForumCommentResponse> res = ResponseAPI.<GetForumCommentResponse>builder()
                    .message("Get reply comments successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<GetForumCommentResponse> res = ResponseAPI.<GetForumCommentResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/vote/forum/{forumId}")
    public ResponseEntity<ResponseAPI<GetVotesResponse>> getVotedByForum(@PathVariable String forumId,

                                                                         @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            GetVotesResponse data = forumService.getVotedByForum(forumId);
            ResponseAPI<GetVotesResponse> res = ResponseAPI.<GetVotesResponse>builder()
                    .message("Get voted by forum successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<GetVotesResponse> res = ResponseAPI.<GetVotesResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }
    @GetMapping(path = "/vote/comment/{commentId}")
    public ResponseEntity<ResponseAPI<GetVotesResponse>> getVoteByComment(@PathVariable String commentId,

                                                                         @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            GetVotesResponse data = forumService.getVoteByComment(commentId);
            ResponseAPI<GetVotesResponse> res = ResponseAPI.<GetVotesResponse>builder()
                    .message("Get vote by comment successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<GetVotesResponse> res = ResponseAPI.<GetVotesResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }
}
