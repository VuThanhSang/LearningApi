package com.example.learning_api.controller;

import com.example.learning_api.dto.request.media.CreateMediaRequest;
import com.example.learning_api.dto.request.media.UpdateMediaRequest;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IMediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MEDIA_BASE_PATH)
public class MediaController {
    private final IMediaService mediaService;
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
}
