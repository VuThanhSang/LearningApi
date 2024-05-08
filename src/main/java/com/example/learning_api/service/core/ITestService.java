package com.example.learning_api.service.core;

import com.example.learning_api.dto.request.test.CreateTestRequest;
import com.example.learning_api.dto.request.test.ImportTestRequest;
import com.example.learning_api.dto.request.test.UpdateTestRequest;
import com.example.learning_api.dto.response.test.CreateTestResponse;
import com.example.learning_api.dto.response.test.GetTestDetailResponse;
import com.example.learning_api.dto.response.test.GetTestsResponse;

public interface ITestService {
    CreateTestResponse createTest(CreateTestRequest body);
    void updateTest(UpdateTestRequest body);
    void deleteTest(String id);
    GetTestsResponse getTests(int page, int size,String search);
    void importTest(ImportTestRequest body);
    GetTestDetailResponse getTestDetail(String id);
}
