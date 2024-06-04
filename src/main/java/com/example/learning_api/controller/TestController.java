package com.example.learning_api.controller;

import com.example.learning_api.constant.StatusCode;
import com.example.learning_api.dto.request.test.*;
import com.example.learning_api.dto.response.teacher.GetTeachersResponse;
import com.example.learning_api.dto.response.test.*;
import com.example.learning_api.model.ResponseAPI;
import com.example.learning_api.service.core.ITestResultService;
import com.example.learning_api.service.core.ITestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.learning_api.constant.RouterConstant.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(TEST_BASE_PATH)
public class TestController {
    private final ITestService testService;
    private final ITestResultService testResultService;

    @PostMapping(path = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<String>> importTest(@ModelAttribute @Valid ImportTestRequest body) {
        try{
            testService.importTest(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Import test successfully")
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
    @GetMapping(path = "/{testId}")
    public ResponseEntity<ResponseAPI<GetTestDetailResponse>> getTestDetail(@PathVariable String testId) {
        try{
            GetTestDetailResponse resData = testService.getTestDetail(testId);
            ResponseAPI<GetTestDetailResponse> res = ResponseAPI.<GetTestDetailResponse>builder()
                    .timestamp(new Date())
                    .message("Get test detail successfully")
                    .data(resData)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetTestDetailResponse> res = ResponseAPI.<GetTestDetailResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @PostMapping(path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseAPI<CreateTestResponse>> createTest(@ModelAttribute @Valid CreateTestRequest body) {
        try{
            CreateTestResponse resDate = testService.createTest(body);
            ResponseAPI<CreateTestResponse> res = ResponseAPI.<CreateTestResponse>builder()
                    .timestamp(new Date())
                    .message("Create test successfully")
                    .data(resDate)
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<CreateTestResponse> res = ResponseAPI.<CreateTestResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }
    @PatchMapping(path = "/{testId}")
    public ResponseEntity<ResponseAPI<String>> updateTest(@RequestBody @Valid UpdateTestRequest body, @PathVariable String testId) {
        try{
            body.setId(testId);
            testService.updateTest(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update test successfully")
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

    @DeleteMapping(path = "/{testId}")
    public ResponseEntity<ResponseAPI<String>> deleteTest(@PathVariable String testId) {
        try{
            testService.deleteTest(testId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete test successfully")
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
    public ResponseEntity<ResponseAPI<GetTestsResponse>> getTests (
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search
    ){
        try{
            GetTestsResponse resData = testService.getTests(page-1, size, search);
            ResponseAPI<GetTestsResponse> res = ResponseAPI.<GetTestsResponse>builder()
                    .timestamp(new Date())
                    .message("Get tests successfully")
                    .data(resData)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetTestsResponse> res = ResponseAPI.<GetTestsResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/in-progress/{studentId}")
    public ResponseEntity<ResponseAPI<GetTestInProgress>> getTestsInProgress (
            @PathVariable String studentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search
    ){
        try{
            GetTestInProgress resData = testService.getTestInProgress( page-1, size,studentId );
            ResponseAPI<GetTestInProgress> res = ResponseAPI.<GetTestInProgress>builder()
                    .timestamp(new Date())
                    .message("Get tests in progress successfully")
                    .data(resData)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetTestInProgress> res = ResponseAPI.<GetTestInProgress>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }
    @GetMapping(path = "/on-specific-day/{studentId}")
    public ResponseEntity<ResponseAPI<GetTestInProgress>> getTestsOnSpecificDayByStudentId (
            @PathVariable String studentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String date
    ){
        try{
            GetTestInProgress resData = testService.getTestOnSpecificDayByStudentId(studentId, date, page-1, size);
            ResponseAPI<GetTestInProgress> res = ResponseAPI.<GetTestInProgress>builder()
                    .timestamp(new Date())
                    .message("Get tests on specific day successfully")
                    .data(resData)
                    .build();
            return new ResponseEntity<>(res, StatusCode.OK);
        }
        catch (Exception e){
            ResponseAPI<GetTestInProgress> res = ResponseAPI.<GetTestInProgress>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/submit")
    public ResponseEntity<ResponseAPI<TestSubmitResponse>> submitTest(@RequestBody @Valid TestSubmitRequest body) {
        try{
            TestSubmitResponse data = testService.submitTest(body);
            ResponseAPI<TestSubmitResponse> res = ResponseAPI.<TestSubmitResponse>builder()
                    .timestamp(new Date())
                    .message("Submit test successfully")
                    .data(data)
                    .build();
            return new ResponseEntity<>(res, StatusCode.CREATED);
        }
        catch (Exception e){
            ResponseAPI<TestSubmitResponse> res = ResponseAPI.<TestSubmitResponse>builder()
                    .timestamp(new Date())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(res, StatusCode.BAD_REQUEST);
        }

    }

    @PostMapping(path = "/result")
    public ResponseEntity<ResponseAPI<String>> addTestResult(@RequestBody @Valid CreateTestResultRequest body) {
        try{
            testResultService.addTestResult(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Add test result successfully")
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

    @PatchMapping(path = "/result/{testResultId}")
    public ResponseEntity<ResponseAPI<String>> updateTestResult(@RequestBody @Valid UpdateTestResultRequest body, @PathVariable String testResultId) {
        try{
            body.setId(testResultId);
            testResultService.updateTestResult(body);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Update test result successfully")
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

    @DeleteMapping(path = "/result/{studentId}/{courseId}")
    public ResponseEntity<ResponseAPI<String>> deleteTestResult(@PathVariable String studentId, @PathVariable String courseId) {
        try{
            testResultService.deleteTestResult(studentId, courseId);
            ResponseAPI<String> res = ResponseAPI.<String>builder()
                    .timestamp(new Date())
                    .message("Delete test result successfully")
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


}
