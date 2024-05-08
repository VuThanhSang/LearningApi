package com.example.learning_api.controller;

import com.example.learning_api.dto.request.substance.CreateSubstanceRequest;
import com.example.learning_api.dto.request.substance.UpdateSubstanceRequest;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.ISubstanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(SUBSTANCE_BASE_PATH)
public class SubstanceController {
    private final ISubstanceService substanceService;
    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<String>> createSubstance(@RequestBody @Valid CreateSubstanceRequest body) {
        try{
            substanceService.createSubstance(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Create substance successfully")
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
    @PatchMapping(path = "/{substanceId}")
    public ResponseEntity<ResponseAPI<String>> updateSubstance(@RequestBody @Valid UpdateSubstanceRequest body, @PathVariable String substanceId) {
        try{
            body.setId(substanceId);
            substanceService.updateSubstance(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update substance successfully")
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
    @DeleteMapping(path = "/{substanceId}")
    public ResponseEntity<ResponseAPI<String>> deleteSubstance(@PathVariable String substanceId) {
        try{
            substanceService.deleteSubstance(substanceId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete substance successfully")
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
