package com.example.learning_api.controller;

import com.example.learning_api.dto.request.summary.CreateSummaryRequest;
import com.example.learning_api.dto.request.summary.UpdateSummaryRequest;
import com.example.learning_api.dto.response.summary.GetSummaryResponse;
import com.example.learning_api.entity.sql.database.SummaryEntity;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.ISummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(SUMMARY_BASE_PATH)
public class SummaryController {
    private final ISummaryService summaryService;

    @PostMapping(path = "")
    public ResponseEntity<ResponseAPI<String>> createSummary(@RequestBody @Valid CreateSummaryRequest body) {
        try{
            summaryService.createSummary(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Create summary successfully")
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

    @PatchMapping(path = "/{summaryId}")
    public ResponseEntity<ResponseAPI<String>> updateSummary(@RequestBody @Valid UpdateSummaryRequest body, @PathVariable String summaryId) {
        try{
            body.setId(summaryId);
            summaryService.updateSummary(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Update summary successfully")
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


    @DeleteMapping(path = "/{summaryId}")
    public ResponseEntity<ResponseAPI<String>> deleteSummary(@PathVariable String summaryId) {
        try{
            summaryService.deleteSummary(summaryId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .message("Delete summary successfully")
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

    @GetMapping(path = "/{summaryId}")
    public ResponseEntity<ResponseAPI<SummaryEntity>> getSummary(@PathVariable String summaryId) {
        try{
            summaryService.getSummary(summaryId);
            ResponseAPI<SummaryEntity> res = ResponseAPI.<SummaryEntity>builder()
                    .message("Get summary successfully")
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<SummaryEntity> res = ResponseAPI.<SummaryEntity>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/student/{studentId}")
    public ResponseEntity<ResponseAPI<List<GetSummaryResponse>>> getSummariesByStudentId(@PathVariable String studentId) {
        try{
            List<GetSummaryResponse> data = summaryService.getSummariesByStudentId(studentId);
            ResponseAPI<List<GetSummaryResponse>> res = ResponseAPI.<List<GetSummaryResponse>>builder()
                    .message("Get summaries by studentId successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<List<GetSummaryResponse>> res = ResponseAPI.<List<GetSummaryResponse>>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

    @GetMapping(path = "/student/{studentId}/term/{termId}")
    public ResponseEntity<ResponseAPI<List<GetSummaryResponse>>> getSummariesByStudentIdAndTermId(@PathVariable String studentId, @PathVariable String termId) {
        try{
            List<GetSummaryResponse> data = summaryService.getSummariesByStudentIdAndTermId(studentId, termId);
            ResponseAPI<List<GetSummaryResponse>> res = ResponseAPI.<List<GetSummaryResponse>>builder()
                    .message("Get summaries by studentId and termId successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        }
        catch (Exception e){
            ResponseAPI<List<GetSummaryResponse>> res = ResponseAPI.<List<GetSummaryResponse>>builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        }
    }

}
