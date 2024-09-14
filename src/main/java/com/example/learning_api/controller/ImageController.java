package com.example.learning_api.controller;

import com.example.learning_api.dto.request.image.RemoveImageRequest;
import com.example.learning_api.dto.request.image.UploadImageRequest;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.IImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/image")
public class ImageController {

    private final IImageService imageService;


    @PostMapping(path = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<List<String>>> uploadImage(@ModelAttribute @Valid UploadImageRequest images) {
        try {
            List<String> urls = imageService.uploadImage(images);
            ResponseAPI<List<String>> res = ResponseAPI.<List<String>>builder()
                    .message("Create forum successfully")
                    .data(urls)
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<List<String>> res = ResponseAPI.<List<String>>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping(path = "/remove")
    public ResponseEntity<ResponseAPI<String>> deleteImage(@RequestBody RemoveImageRequest url) {
        try {
            imageService.deleteImage(url);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete image successfully")
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }


}
