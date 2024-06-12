package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.CloudinaryConstant;
import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.request.test.CreateTestRequest;
import com.example.learning_api.dto.request.test.ImportTestRequest;
import com.example.learning_api.dto.request.test.TestSubmitRequest;
import com.example.learning_api.dto.request.test.UpdateTestRequest;
import com.example.learning_api.dto.response.CloudinaryUploadResponse;
import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import com.example.learning_api.dto.response.test.*;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.ImportTestType;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.CloudinaryService;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ITestService;
import com.example.learning_api.utils.ImageUtils;
import com.example.learning_api.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Slice;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService implements ITestService {
    private final ModelMapperService modelMapperService;
    private final TestRepository testRepository;
    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CloudinaryService cloudinaryService;
    @Override
    public CreateTestResponse createTest(CreateTestRequest body) {
        try{
            UserEntity userEntity = userRepository.findById(body.getCreatedBy())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            if (!ImageUtils.isValidImageFile(body.getSource()) && body.getSource()!=null) {
                throw new CustomException(ErrorConstant.IMAGE_INVALID);
            }
            if (body.getCreatedBy()==null){
                throw new IllegalArgumentException("UserId is required");
            }
            if (userEntity==null){
                throw new IllegalArgumentException("UserId is not found");
            }
            if (body.getCourseId() == null){
                throw new IllegalArgumentException("CourseId is required");
            }
            if (courseRepository.findById(body.getCourseId()).isEmpty()){
                throw new IllegalArgumentException("CourseId is not found");
            }
            if (body.getStartTime()==null){
                throw new IllegalArgumentException("Start time is required");
            }
            if (body.getEndTime()==null){
                throw new IllegalArgumentException("End time is required");
            }
            CreateTestResponse resData = new CreateTestResponse();
            TestEntity testEntity = modelMapperService.mapClass(body, TestEntity.class);
            if(body.getSource()!=null){
                byte[] originalImage = new byte[0];
                originalImage = body.getSource().getBytes();
                byte[] newImage = ImageUtils.resizeImage(originalImage, 200, 200);
                CloudinaryUploadResponse imageUploaded = cloudinaryService.uploadFileToFolder(
                        CloudinaryConstant.CLASSROOM_PATH,
                        StringUtils.generateFileName(body.getName(), "tests"),
                        newImage,
                        "image"
                );
                testEntity.setSource(imageUploaded.getUrl());
            }
            testEntity.setCreatedAt(new Date());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date startdate = formatter.parse(body.getStartTime());
            Date enddate = formatter.parse(body.getEndTime());
            testEntity.setStartTime(startdate);
            testEntity.setEndTime(enddate);
            testEntity.setUpdatedAt(new Date());
            testRepository.save(testEntity);
            resData.setCreatedBy(body.getCreatedBy());
            resData.setCreatedAt(testEntity.getCreatedAt().toString());
            resData.setDescription(body.getDescription());
            resData.setDuration(body.getDuration());
            resData.setId(testEntity.getId());
            resData.setSource(testEntity.getSource());
            resData.setName(body.getName());
            resData.setUpdatedAt(testEntity.getUpdatedAt().toString());
            return resData;

        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void updateTest(UpdateTestRequest body) {
        try{
            TestEntity testEntity = testRepository.findById(body.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Test not found"));
            if (body.getId()==null){
                throw new IllegalArgumentException("TestId is required");
            }
            if (testEntity==null){
                throw new IllegalArgumentException("TestId is not found");
            }
            testEntity.setUpdatedAt(new Date());
            if (body.getName()!=null){
                testEntity.setName(body.getName());
            }
            if (body.getDescription()!=null){
                testEntity.setDescription(body.getDescription());
            }
            if (body.getDuration()!=0){
                testEntity.setDuration(body.getDuration());
            }
            testRepository.save(testEntity);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public void deleteTest(String id) {
        try{
            TestEntity testEntity = testRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Test not found"));
            cloudinaryService.deleteImage(testEntity.getSource());
            List<QuestionEntity> questionEntities = questionRepository.findByTestId(id);
            for (QuestionEntity questionEntity : questionEntities){
                answerRepository.deleteByQuestionId(questionEntity.getId());
            }
            questionRepository.deleteByTestId(id);
            testRepository.deleteById(id);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Override
    public GetTestsResponse getTests(int page, int size, String search) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Page<TestEntity> testEntities = testRepository.findByNameContaining(search, pageAble);
            GetTestsResponse resData = new GetTestsResponse();
            List<GetTestsResponse.TestResponse> testResponses = new ArrayList<>();
            for (TestEntity testEntity : testEntities){
                GetTestsResponse.TestResponse testResponse = modelMapperService.mapClass(testEntity, GetTestsResponse.TestResponse.class);
                testResponses.add(testResponse);
            }
            resData.setTests(testResponses);
            resData.setTotalElements(testEntities.getTotalElements());
            resData.setTotalPage(testEntities.getTotalPages());
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void importTest(ImportTestRequest body) {
        try {
            String fileContent;
            if (body.getType()== ImportTestType.FILE){
                String fileName = body.getFile().getOriginalFilename();
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();


                if (fileExtension.equals("pdf")) {
                    PDDocument document = PDDocument.load(body.getFile().getInputStream());
                    PDFTextStripper stripper = new PDFTextStripper();
                    fileContent = stripper.getText(document);
                } else if (fileExtension.equals("docx")) {
                    XWPFDocument document = new XWPFDocument(body.getFile().getInputStream());
                    StringBuilder content = new StringBuilder();
                    List<XWPFParagraph> paragraphs = document.getParagraphs();
                    for (XWPFParagraph paragraph : paragraphs) {
                        content.append(paragraph.getText()).append("\n");
                    }
                    fileContent = content.toString();
                } else {
                    throw new CustomException(ErrorConstant.FILE_INVALID);
                }
            }
            else{
                fileContent = body.getText();
            }

            Pattern questionPattern = Pattern.compile("Câu\\s+(\\d+)\\s*:\\s*([^\\n]+)\\n([\\s\\S]+?)(?=\\nCâu\\s+\\d+\\s*:|$)");
            Pattern answerPattern = Pattern.compile("\\b([A-D])\\.\\s*(.+?)(\\*?)(?=(?:\\n[A-D]\\.|$))", Pattern.DOTALL);
            Matcher questionMatcher = questionPattern.matcher(fileContent);
            Matcher answerMatcher;
            List<QuestionEntity> questions = new ArrayList<>();
            while (questionMatcher.find()) {
                String questionNumber = questionMatcher.group(1);
                String questionText = questionMatcher.group(2).trim();
                QuestionEntity question = new QuestionEntity();
                question.setContent(questionText);
                question.setTestId(body.getTestId());
                question.setSource("");
                question.setCreatedAt(new Date());
                question.setUpdatedAt(new Date());
                questionRepository.save(question);
                String answerGroup = questionMatcher.group(3);
                answerMatcher = answerPattern.matcher(answerGroup);

                while (answerMatcher.find()) {
                    String answerChoice = answerMatcher.group(1);
                    String answerText = answerMatcher.group(2).trim();
                    String isCorrect = answerMatcher.group(3);

                    AnswerEntity answer = new AnswerEntity();
                    answer.setContent(answerText);
                    answer.setQuestionId(question.getId());
                    answer.setSource("");
                    answer.setCreatedAt(new Date());
                    answer.setUpdatedAt(new Date());
                    answer.setCorrect(!isCorrect.isEmpty());
                    answerRepository.save(answer);
                }


            }

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetTestDetailResponse getTestDetail(String id) {
        try{
            TestEntity testEntity = testRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Test not found"));
            if (testEntity==null){
                throw new IllegalArgumentException("TestId is not found");
            }
            GetTestDetailResponse resData = new GetTestDetailResponse();
            resData.setId(testEntity.getId());
            resData.setName(testEntity.getName());
            resData.setDescription(testEntity.getDescription());
            resData.setDuration(testEntity.getDuration());
            resData.setSource(testEntity.getSource());
            List<GetQuestionsResponse.QuestionResponse> questionResponses = new ArrayList<>();
            List<QuestionEntity> questionEntities = questionRepository.findByTestId(id);
            for (QuestionEntity questionEntity : questionEntities){
                GetQuestionsResponse.QuestionResponse questionResponse = modelMapperService.mapClass(questionEntity, GetQuestionsResponse.QuestionResponse.class);
                List<AnswerEntity> answerEntities = answerRepository.findByQuestionId(questionEntity.getId());
                List<GetQuestionsResponse.AnswerResponse> answerResponses = new ArrayList<>();
                for (AnswerEntity answerEntity : answerEntities){
                    GetQuestionsResponse.AnswerResponse answerResponse = modelMapperService.mapClass(answerEntity, GetQuestionsResponse.AnswerResponse.class);
                    answerResponses.add(answerResponse);
                }
                questionResponse.setAnswers(answerResponses);
                questionResponses.add(questionResponse);
            }
            resData.setQuestions(questionResponses);
            resData.setTotalQuestions(questionResponses.size());
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public GetTestInProgress getTestInProgress(int page,int size,String studentId) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Slice<TestEntity> testEntities = testRepository.findTestInProgressByStudentId(studentId, pageAble);
            GetTestInProgress resData = new GetTestInProgress();
            List<GetTestInProgress.TestResponse> testResponses = new ArrayList<>();
            for (TestEntity testEntity : testEntities){
                GetTestInProgress.TestResponse testResponse = modelMapperService.mapClass(testEntity, GetTestInProgress.TestResponse.class);
                testResponses.add(testResponse);
            }
            resData.setTests(testResponses);
            resData.setTotalElements((long) testEntities.getNumberOfElements());
            resData.setTotalPage(testEntities.getNumber());
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public GetTestInProgress getTestOnSpecificDayByStudentId(String studentId, String date, int page, int size) {
        try{
            Pageable pageAble = PageRequest.of(page, size);
            Slice<TestEntity> testEntities = testRepository.findTestsOnSpecificDateByStudentId(studentId, date, pageAble);
            GetTestInProgress resData = new GetTestInProgress();
            List<GetTestInProgress.TestResponse> testResponses = new ArrayList<>();
            for (TestEntity testEntity : testEntities){
                GetTestInProgress.TestResponse testResponse = modelMapperService.mapClass(testEntity, GetTestInProgress.TestResponse.class);
                testResponses.add(testResponse);
            }
            resData.setTests(testResponses);
            resData.setTotalElements((long) testEntities.getNumberOfElements());
            resData.setTotalPage(testEntities.getNumber());
            return resData;
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public TestSubmitResponse submitTest( TestSubmitRequest body) {
        TestResultEntity testResult = testResultRepository.findByStudentIdAndTestId(body.getStudentId(), body.getTestId());
        if(testResult!=null){
            throw new CustomException(ErrorConstant.TEST_SUBMITTED);
        }
        GetTestDetailResponse testDetail = getTestDetail(body.getTestId());
        List<GetQuestionsResponse.QuestionResponse> questions = testDetail.getQuestions();
        List<List<Integer>> correctAnswers = new ArrayList<>();
        int result = 0;
        for (GetQuestionsResponse.QuestionResponse question : questions) {
            int correctCount = 0;
            int answerCorrectCount = 0;
            List<GetQuestionsResponse.AnswerResponse> answers = question.getAnswers();
            List<Integer> correctAnswer = new ArrayList<>();
            for (GetQuestionsResponse.AnswerResponse answer : answers) {
                if (answer.isCorrect()) {
                    correctCount++;
                    correctAnswer.add(answers.indexOf(answer));
                    if (body.getAnswers().size() > questions.indexOf(question)){
                        if (body.getAnswers().get(questions.indexOf(question)).contains(answers.indexOf(answer))) {
                            answerCorrectCount++;
                        }
                    }
                }
            }
            correctAnswers.add(correctAnswer);
            if (correctCount == answerCorrectCount) {
                result++;
            }
        }
        TestResultEntity testResultEntity = new TestResultEntity();
        testResultEntity.setTestId(body.getTestId());
        testResultEntity.setStudentId(body.getStudentId());
        testResultEntity.setGrade(result);
        testResultEntity.setTestType("test");
        testResultEntity.setAttendedAt(new Date());
        testResultEntity.setCreatedAt(new Date());
        testResultEntity.setUpdatedAt(new Date());
        testResultRepository.save(testResultEntity);
        TestSubmitResponse resData = new TestSubmitResponse();
        resData.setTestType("test");
        resData.setStudentId(body.getStudentId());
        resData.setTestId(body.getTestId());
        resData.setAttendedAt(testResultEntity.getAttendedAt());
        resData.setTotalCorrectAnswers(result);
        resData.setTotalQuestions(questions.size());
        resData.setAnswers(correctAnswers);
        double grade = (double) result / questions.size() * 10;
        grade = Math.round(grade * 100.0) / 100.0;
        resData.setGrade(grade);
        return resData;
    }

}
