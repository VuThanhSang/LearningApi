package com.example.learning_api.controller;

import com.example.learning_api.dto.request.media.CreateMediaRequest;
import com.example.learning_api.dto.request.media.UpdateMediaRequest;
import com.example.learning_api.dto.response.media.GetMediaCommentsResponse;
import com.example.learning_api.dto.response.media.GetMediaDetailResponse;
import com.example.learning_api.dto.response.media.GetMediaNotesResponse;
import com.example.learning_api.dto.response.media.GetMediaResponse;
import com.example.learning_api.entity.sql.database.MediaCommentEntity;
import com.example.learning_api.entity.sql.database.MediaEntity;
import com.example.learning_api.entity.sql.database.MediaNoteEntity;
import com.example.learning_api.entity.sql.database.MediaProgressEntity;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.repository.database.StudentRepository;
import com.example.learning_api.repository.database.TeacherRepository;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.core.IMediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MEDIA_BASE_PATH)
public class MediaController {
    private final IMediaService mediaService;
    private final JwtService jwtService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> uploadMedia(@ModelAttribute @Valid CreateMediaRequest body) {
        try{
            mediaService.createMedia(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Upload media successfully")
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

    @PatchMapping(path = "/{mediaId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> updateMedia(@ModelAttribute @Valid UpdateMediaRequest body, @PathVariable String mediaId) {
        try{
            body.setId(mediaId);
            mediaService.updateMedia(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update media successfully")
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

    @DeleteMapping(path = "/{mediaId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> deleteMedia(@PathVariable String mediaId) {
        try{
            mediaService.deleteMedia(mediaId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete media successfully")
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

    @GetMapping(path = "/{mediaId}")
    public ResponseEntity<ResponseAPI<GetMediaDetailResponse>> getMedia(@PathVariable String mediaId) {
        try{
           GetMediaDetailResponse data =  mediaService.getMedia(mediaId);
            ResponseAPI<GetMediaDetailResponse> res = ResponseAPI.<GetMediaDetailResponse>builder()
                    .message("Get media successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<GetMediaDetailResponse> res = ResponseAPI.<GetMediaDetailResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/lesson/{lessonId}")
    public ResponseEntity<ResponseAPI<GetMediaResponse>> getMediaByLessonId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String lessonId
    ) {
        try{
            GetMediaResponse data= mediaService.getMediaByLessonId(lessonId, page-1, size);
            ResponseAPI<GetMediaResponse> res = ResponseAPI.<GetMediaResponse>builder()
                    .message("Get media by lessonId successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<GetMediaResponse> res = ResponseAPI.<GetMediaResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/classroom/{classroomId}")
    public ResponseEntity<ResponseAPI<GetMediaResponse>> getMediaByClassroomId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String classroomId
    ) {
        try{
            GetMediaResponse data= mediaService.getMediaByClassroomId(classroomId, page-1, size);
            ResponseAPI<GetMediaResponse> res = ResponseAPI.<GetMediaResponse>builder()
                    .message("Get media by classroomId successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<GetMediaResponse> res = ResponseAPI.<GetMediaResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PatchMapping(path = "/progress")
    public ResponseEntity<ResponseAPI<String>> updateMediaProgress(@RequestBody @Valid MediaProgressEntity body) {
        try{
            mediaService.updateMediaProgress(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update media progress successfully")
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

    @GetMapping(path = "/progress/{userId}/{mediaId}")
    public ResponseEntity<ResponseAPI<MediaProgressEntity>> getMediaProgress(
            @PathVariable String userId,
            @PathVariable String mediaId
    ) {
        try{
            MediaProgressEntity data = mediaService.getMediaProgress(userId, mediaId);
            ResponseAPI<MediaProgressEntity> res = ResponseAPI.<MediaProgressEntity>builder()
                    .message("Get media progress successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<MediaProgressEntity> res = ResponseAPI.<MediaProgressEntity>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping(path = "/comment")
    public ResponseEntity<ResponseAPI<String>> createMediaComment(@RequestBody @Valid MediaCommentEntity body) {
        try{
            mediaService.createMediaComment(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Create media comment successfully")
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

    @PatchMapping(path = "/comment/{commentId}")
    public ResponseEntity<ResponseAPI<String>> updateMediaComment(@RequestBody @Valid MediaCommentEntity body, @PathVariable String commentId) {
        try{
            body.setId(commentId);
            mediaService.updateMediaComment(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update media comment successfully")
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
    public ResponseEntity<ResponseAPI<String>> deleteMediaComment(@PathVariable String commentId) {
        try{
            mediaService.deleteMediaComment(commentId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete media comment successfully")
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

    @GetMapping(path = "/comment/{commentId}")
    public ResponseEntity<ResponseAPI<MediaCommentEntity>> getMediaComment(@PathVariable String commentId) {
        try{
            MediaCommentEntity data = mediaService.getMediaComment(commentId);
            ResponseAPI<MediaCommentEntity> res = ResponseAPI.<MediaCommentEntity>builder()
                    .message("Get media comment successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<MediaCommentEntity> res = ResponseAPI.<MediaCommentEntity>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/comment/media/{mediaId}")
    public ResponseEntity<ResponseAPI<GetMediaCommentsResponse>> getMediaCommentByMediaId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String mediaId
    ) {
        try{
            GetMediaCommentsResponse data = mediaService.getMediaCommentByMediaId(mediaId, page-1, size);
            ResponseAPI<GetMediaCommentsResponse> res = ResponseAPI.<GetMediaCommentsResponse>builder()
                    .message("Get media comment by mediaId successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<GetMediaCommentsResponse> res = ResponseAPI.<GetMediaCommentsResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/comment/user/{userId}")
    public ResponseEntity<ResponseAPI<GetMediaCommentsResponse>> getMediaCommentByUserId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String userId
    ) {
        try{
            GetMediaCommentsResponse data = mediaService.getMediaCommentByUserId(userId, page-1, size);
            ResponseAPI<GetMediaCommentsResponse> res = ResponseAPI.<GetMediaCommentsResponse>builder()
                    .message("Get media comment by userId successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<GetMediaCommentsResponse> res = ResponseAPI.<GetMediaCommentsResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping(path = "/note")
    public ResponseEntity<ResponseAPI<String>> createMediaNote(@RequestBody @Valid MediaNoteEntity body,@RequestHeader("Authorization") String authorizationHeader){
        try{
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            String id = "";
            if(role.equals("USER")){
                id = studentRepository.findByUserId(userId).getId();
            }
            else if(role.equals("TEACHER")){
                id = teacherRepository.findByUserId(userId).getId();
            }
            body.setUserId(id);
            body.setRole(RoleEnum.valueOf(role));
            mediaService.createMediaNote(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Create media note successfully")
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

    @PatchMapping(path = "/note/{noteId}")
    public ResponseEntity<ResponseAPI<String>> updateMediaNote(@RequestBody @Valid MediaNoteEntity body, @PathVariable String noteId) {
        try{
            body.setId(noteId);
            mediaService.updateMediaNote(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update media note successfully")
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

    @DeleteMapping(path = "/note/{noteId}")
    public ResponseEntity<ResponseAPI<String>> deleteMediaNote(@PathVariable String noteId) {
        try{
            mediaService.deleteMediaNote(noteId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete media note successfully")
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

    @GetMapping(path = "/note/{noteId}")
    public ResponseEntity<ResponseAPI<MediaNoteEntity>> getMediaNote(@PathVariable String noteId) {
        try{
            MediaNoteEntity data = mediaService.getMediaNote(noteId);
            ResponseAPI<MediaNoteEntity> res = ResponseAPI.<MediaNoteEntity>builder()
                    .message("Get media note successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<MediaNoteEntity> res = ResponseAPI.<MediaNoteEntity>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/note/media/{mediaId}")
    public ResponseEntity<ResponseAPI<GetMediaNotesResponse>> getMediaNoteByMediaId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String mediaId
    ) {
        try{
            GetMediaNotesResponse data = mediaService.getMediaNoteByMediaId(mediaId, page-1, size);
            ResponseAPI<GetMediaNotesResponse> res = ResponseAPI.<GetMediaNotesResponse>builder()
                    .message("Get media note by mediaId successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<GetMediaNotesResponse> res = ResponseAPI.<GetMediaNotesResponse>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/{mediaId}/note")
    public ResponseEntity<ResponseAPI<List<GetMediaDetailResponse.TimeGroupedNotes>>> getMediaNoteByUserId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String mediaId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try{
            String userId = extractUserId(authorizationHeader);
            String role = extractRole(authorizationHeader);
            String id;
            if (role.equals("USER")){
                id = studentRepository.findByUserId(userId).getId();
            }
            else {
                id = teacherRepository.findByUserId(userId).getId();
            }
            List<GetMediaDetailResponse.TimeGroupedNotes> data = mediaService.getMediaNoteByUserIdAndMediaId(id,role,mediaId, page-1, size);
            ResponseAPI<List<GetMediaDetailResponse.TimeGroupedNotes>> res = ResponseAPI.<List<GetMediaDetailResponse.TimeGroupedNotes>>builder()
                    .message("Get media note by userId successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<List<GetMediaDetailResponse.TimeGroupedNotes>> res = ResponseAPI.<List<GetMediaDetailResponse.TimeGroupedNotes>>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }
    private String extractUserId(String authorizationHeader) throws Exception {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        return jwtService.extractUserId(accessToken);
    }

    private String extractRole(String authorizationHeader) throws Exception {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        return jwtService.extractRole(accessToken);
    }



}
