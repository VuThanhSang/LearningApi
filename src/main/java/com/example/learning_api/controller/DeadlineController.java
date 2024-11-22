package com.example.learning_api.controller;
import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.deadline.*;
import com.example.learning_api.dto.response.classroom.ClassroomDeadlineResponse;
import com.example.learning_api.dto.response.deadline.*;
import com.example.learning_api.entity.sql.database.DeadlineEntity;
import com.example.learning_api.entity.sql.database.DeadlineSubmissionsEntity;
import com.example.learning_api.entity.sql.database.ScoringCriteriaEntity;
import com.example.learning_api.enums.DeadlineSubmissionStatus;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.common.ExcelExportService;
import com.example.learning_api.service.common.JwtService;
import com.example.learning_api.service.core.IDeadlineService;
import com.example.learning_api.service.core.IDeadlineSubmissionsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(DEADLINE_BASE_PATH)
@Slf4j
public class DeadlineController {
    private final IDeadlineService deadlineService;
    private final IDeadlineSubmissionsService deadlineSubmissionsService;
    private final JwtService jwtService;
    @Autowired
    private ExcelExportService excelExportService;
    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> createDeadline(@ModelAttribute @Valid CreateDeadlineRequest body) {
        try{
            deadlineService.createDeadline(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create deadline successfully")
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

    @PatchMapping(path = "/{deadlineId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> updateDeadline(@ModelAttribute @Valid UpdateDeadlineRequest body, @PathVariable String deadlineId) {
        try{
            body.setId(deadlineId);
            deadlineService.updateDeadline(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update deadline successfully")
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


    @DeleteMapping(path = "/{deadlineId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> deleteDeadline(@PathVariable String deadlineId) {
        try{
             deadlineService.deleteDeadline(deadlineId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete deadline successfully")
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

    @GetMapping(path = "/{deadlineId}")
    public ResponseEntity<ResponseAPI<DeadlineResponse>> getDeadline(@PathVariable String deadlineId) {
        try{
           DeadlineResponse data =  deadlineService.getDeadline(deadlineId);
            ResponseAPI<DeadlineResponse> res = ResponseAPI.<DeadlineResponse>builder()
                    .timestamp(new Date())
                    .message("Get deadline successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<DeadlineResponse> res = ResponseAPI.<DeadlineResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/lesson/{lessonId}")
    public ResponseEntity<ResponseAPI<GetDeadlinesResponse>> getDeadlines(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String lessonId,
            @RequestHeader(value = "Authorization") String authorizationHeader){
        try{
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String userId = jwtService.extractUserId(accessToken);
            String extractAuthoritiesFromClaim = jwtService.extractRole(accessToken);
            GetDeadlinesResponse data =  deadlineService.getDeadlinesByLessonId(lessonId, page-1, size,extractAuthoritiesFromClaim);
            ResponseAPI<GetDeadlinesResponse> res = ResponseAPI.<GetDeadlinesResponse>builder()
                    .timestamp(new Date())
                    .message("Get deadlines successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetDeadlinesResponse> res = ResponseAPI.<GetDeadlinesResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/upcoming/{studentId}")
    public ResponseEntity<ResponseAPI<GetUpcomingDeadlineResponse>> getUpcomingDeadlinesByStudentId(
            @PathVariable String studentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filterType
            )  {
        try{
            GetUpcomingDeadlineResponse data =  deadlineService.getUpcomingDeadlineByStudentId(studentId,filterType, page-1, size);
            ResponseAPI<GetUpcomingDeadlineResponse> res = ResponseAPI.<GetUpcomingDeadlineResponse>builder()
                    .timestamp(new Date())
                    .message("Get upcoming deadlines successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetUpcomingDeadlineResponse> res = ResponseAPI.<GetUpcomingDeadlineResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @GetMapping(path="/classroom/{classroomId}")
    public ResponseEntity<ResponseAPI<ClassroomDeadlineResponse>> getClassroomDeadlinesByClassroomId(@PathVariable String classroomId,
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "10") int size,
                                                                                                     @RequestHeader(value = "Authorization") String authorizationHeader
    ){
        try{
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String role = jwtService.extractRole(accessToken);
            ClassroomDeadlineResponse data = deadlineService.getClassroomDeadlinesByClassroomId(classroomId, page-1, size,role);
            ResponseAPI<ClassroomDeadlineResponse> res = ResponseAPI.<ClassroomDeadlineResponse>builder()
                    .timestamp(new Date())
                    .message("Get classroom deadlines successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<ClassroomDeadlineResponse> res = ResponseAPI.<ClassroomDeadlineResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/statistics/{classroomId}")
    public ResponseEntity<ResponseAPI<GetDeadlineStatistics>> getDeadlineStatistics(@PathVariable String classroomId,
                                                                                      @RequestParam(defaultValue = "1") int page,
                                                                                      @RequestParam(defaultValue = "10") int size) {
        try {
            GetDeadlineStatistics data = deadlineService.getDeadlineStatistics(classroomId, page-1, size);
            ResponseAPI<GetDeadlineStatistics> res = ResponseAPI.<GetDeadlineStatistics>builder()
                    .timestamp(new Date())
                    .message("Get deadline statistics successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ResponseAPI<GetDeadlineStatistics> res = ResponseAPI.<GetDeadlineStatistics>builder()
                    .timestamp(new Date())
                    .message("An unexpected error occurred: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
    
    @GetMapping(path = "/statistics/{classroomId}/export")
    public ResponseEntity<InputStreamResource> exportExcel(@PathVariable String classroomId) throws IOException {
        GetDeadlineStatistics statistics = deadlineService.getDeadlineStatistics(classroomId, 0, Integer.MAX_VALUE);

        ByteArrayInputStream in = excelExportService.exportDeadlineStatisticsToExcel(statistics.getDeadlineStatistics());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=deadline_statistics.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
    
    @GetMapping(path = "/teacher/{teacherId}")
    public ResponseEntity<ResponseAPI<GetDeadlinesResponse>> getDeadlinesByTeacherId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String teacherId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    )  {
        try {
            if (search== "") search = null;
            if (status == "") status = null;
            if (startDate == "") startDate = null;
            if (endDate == "") endDate = null;
            GetDeadlinesResponse data = deadlineService.getDeadlinesByTeacherId(teacherId, search, status, startDate, endDate, page-1, size);
            ResponseAPI<GetDeadlinesResponse> res = ResponseAPI.<GetDeadlinesResponse>builder()
                    .timestamp(new Date())
                    .message("Get deadlines by teacherId successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            ResponseAPI<GetDeadlinesResponse> res = ResponseAPI.<GetDeadlinesResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        } catch (Exception e) {
            ResponseAPI<GetDeadlinesResponse> res = ResponseAPI.<GetDeadlinesResponse>builder()
                    .timestamp(new Date())
                    .message("An unexpected error occurred: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @PostMapping(path = "/{deadlineId}/submissions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<ResponseAPI<String>> createDeadlineSubmissions(@ModelAttribute @Valid CreateDeadlineSubmissionsRequest body){
        try{
            body.setDeadlineId(body.getDeadlineId());
            deadlineSubmissionsService.CreateDeadlineSubmissions(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create deadline submissions successfully")
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

    @PostMapping(path = "/{deadlineId}/submissions/{submissionId}/grade")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> gradeDeadlineSubmissions(@PathVariable String submissionId, @RequestBody @Valid GradeSubmissionRequest body) {
        try{
            deadlineSubmissionsService.GradeDeadlineSubmissions(submissionId, body.getGrade(), body.getFeedback());
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Grade deadline submissions successfully")
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
    @PatchMapping(path = "/{deadlineId}/submissions/{submissionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> updateDeadlineSubmissions(@ModelAttribute @Valid UpdateDeadlineSubmissionsRequest body, @PathVariable String submissionId) {
        try{
            body.setId(submissionId);
            deadlineSubmissionsService.UpdateDeadlineSubmissions(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update deadline submissions successfully")
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

    @DeleteMapping(path = "/{deadlineId}/submissions/{submissionId}")
    public ResponseEntity<ResponseAPI<String>> deleteDeadlineSubmissions(@PathVariable String submissionId) {
        try{
            deadlineSubmissionsService.DeleteDeadlineSubmissions(submissionId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete deadline submissions successfully")
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

    @GetMapping(path = "/{deadlineId}/submissions/{submissionId}")
    public ResponseEntity<ResponseAPI<DeadlineSubmissionResponse>> getDeadlineSubmissions(@PathVariable String submissionId) {
        try{
            DeadlineSubmissionResponse data =  deadlineSubmissionsService.GetDeadlineSubmissions(submissionId);
            ResponseAPI<DeadlineSubmissionResponse> res = ResponseAPI.<DeadlineSubmissionResponse>builder()
                    .timestamp(new Date())
                    .message("Get deadline submissions successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<DeadlineSubmissionResponse> res = ResponseAPI.<DeadlineSubmissionResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @GetMapping(path = "/{deadlineId}/submissions/student/{studentId}")
    public ResponseEntity<ResponseAPI<GetDeadlineSubmissionsResponse>> getDeadlineSubmissionsByStudentId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String deadlineId,
            @PathVariable String studentId)  {
        try{
            GetDeadlineSubmissionsResponse data =  deadlineSubmissionsService.GetDeadlineSubmissionsByStudentId(studentId,deadlineId, page-1, size);
            ResponseAPI<GetDeadlineSubmissionsResponse> res = ResponseAPI.<GetDeadlineSubmissionsResponse>builder()
                    .timestamp(new Date())
                    .message("Get deadline submissions by studentId successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetDeadlineSubmissionsResponse> res = ResponseAPI.<GetDeadlineSubmissionsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @GetMapping(path = "/{deadlineId}/submissions")
    public ResponseEntity<ResponseAPI<GetDeadlineSubmissionsResponse>> getDeadlineSubmissionsByDeadlineId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String deadlineId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection)  {
        try {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            // Convert empty strings, "," or null to null
            search = (search == null || search.trim().isEmpty() || search.equals(",")) ? null : search.trim();
            status = (status == null || status.trim().isEmpty() || status.equals(",")) ? null : status.trim();
            sortBy = (sortBy == null || sortBy.trim().isEmpty() || sortBy.equals(",")) ? null : sortBy.trim();

            GetDeadlineSubmissionsResponse data = deadlineSubmissionsService.GetDeadlineSubmissionsByDeadlineId(
                    deadlineId, page-1, size, search, status, sortBy, direction);

            ResponseAPI<GetDeadlineSubmissionsResponse> res = ResponseAPI.<GetDeadlineSubmissionsResponse>builder()
                    .timestamp(new Date())
                    .message("Get deadline submissions by deadlineId successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ResponseAPI<GetDeadlineSubmissionsResponse> res = ResponseAPI.<GetDeadlineSubmissionsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseAPI<GetDeadlineSubmissionsResponse> res = ResponseAPI.<GetDeadlineSubmissionsResponse>builder()
                    .timestamp(new Date())
                    .message("An unexpected error occurred: " + e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping(path = "/student/{studentId}")
    public ResponseEntity<ResponseAPI<GetDeadlinesResponse>> getDeadlinesByStudentId(
            @RequestParam(defaultValue = "1 ") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String studentId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String classroomId,
            @RequestParam(required = false, defaultValue = "startDate") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder
    )  {
        try {
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            GetDeadlinesResponse data = deadlineService.getDeadlinesByStudentId(
                    studentId, search, status, startDate, endDate, classroomId, page-1, size, sortBy, direction);

            ResponseAPI<GetDeadlinesResponse> res = ResponseAPI.<GetDeadlinesResponse>builder()
                    .timestamp(new Date())
                    .message("Get deadlines by studentId successfully")
                    .data(data)
                    .build();
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            ResponseAPI<GetDeadlinesResponse> res = ResponseAPI.<GetDeadlinesResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(res);
        } catch (Exception e) {
            ResponseAPI<GetDeadlinesResponse> res = ResponseAPI.<GetDeadlinesResponse>builder()
                    .timestamp(new Date())
                    .message("An unexpected error occurred: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @GetMapping("/download/{deadlineId}")
    public ResponseEntity<byte[]> downloadDeadlineSubmissionFiles(@PathVariable String deadlineId) {
        try {
            List<String> fileUrls = deadlineSubmissionsService.downloadSubmission(deadlineId, DeadlineSubmissionStatus.SUBMITTED);
            byte[] zipBytes = createZipFile(fileUrls);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=deadline-submission-files.zip");

            return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error downloading deadline submission files: " + e.getMessage());
        }
    }

    private byte[] createZipFile(List<String> fileUrls) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (String fileUrl : fileUrls) {
                byte[] fileBytes = downloadFileFromUrl(fileUrl);
                String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                zos.putNextEntry(new ZipEntry(fileName));
                zos.write(fileBytes);
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        }
    }

    private byte[] downloadFileFromUrl(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return connection.getInputStream().readAllBytes();
        } else {
            throw new IOException("Failed to download file from URL: " + fileUrl);
        }
    }


    @PostMapping(path = "/scoring-criteria")
//    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> createScoringCriteria(@RequestBody ScoringCriteriaEntity body) {
        try{
            deadlineService.createScoringCriteria(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Create scoring criteria successfully")
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

    @PatchMapping(path = "/scoring-criteria/{scoringCriteriaId}")
//    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> updateScoringCriteria(@RequestBody ScoringCriteriaEntity body, @PathVariable String scoringCriteriaId) {
        try{
            deadlineService.updateScoringCriteria(body, scoringCriteriaId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update scoring criteria successfully")
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

    @DeleteMapping(path = "/scoring-criteria/{scoringCriteriaId}")
//    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseEntity<ResponseAPI<String>> deleteScoringCriteria(@PathVariable String scoringCriteriaId) {
        try{
            deadlineService.deleteScoringCriteria(scoringCriteriaId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete scoring criteria successfully")
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

    @GetMapping("/download-submissions/{deadlineId}")
    public ResponseEntity<byte[]> downloadDeadlineSubmissionsExcel(@PathVariable String deadlineId) {
        try {
            byte[] excelBytes = deadlineSubmissionsService.downloadDeadlineSubmissionsByStudentId(deadlineId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("deadline-submissions.xlsx")
                    .build());

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error downloading deadline submissions excel: " + e.getMessage());
        }
    }




}
