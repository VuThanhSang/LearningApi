package com.example.learning_api.service.core.Impl;

import com.example.learning_api.dto.request.test.CreateTestResultRequest;
import com.example.learning_api.dto.request.test.SaveProgressRequest;
import com.example.learning_api.dto.request.test.UpdateTestResultRequest;
import com.example.learning_api.dto.response.question.GetQuestionsResponse;
import com.example.learning_api.dto.response.test.*;
import com.example.learning_api.entity.sql.database.*;
import com.example.learning_api.enums.FileOwnerType;
import com.example.learning_api.enums.QuestionType;
import com.example.learning_api.enums.TestState;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.ITestResultService;
import com.example.learning_api.service.core.ITestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestResultService implements ITestResultService {
    private final TestResultRepository testResultRepository;
    private final StudentRepository studentRepository;
    private final ModelMapperService modelMapperService;
    private final TestRepository testRepository;
    private final StudentAnswersRepository studentAnswerRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final StudentEnrollmentsRepository studentEnrollmentsRepository;
    private final ClassRoomRepository classRoomRepository;
    private final ITestService testService;
    private final FileRepository fileRepository;
    @Override
    public StartTestResponse addTestResult(CreateTestResultRequest body) {
        try{
            if (body.getTestId()==null || body.getStudentId()==null ) {
                throw new IllegalArgumentException("Test id, student id  must be provided");
            }
            if (testRepository.existsById(body.getTestId()) == false) {
                throw new IllegalArgumentException("Test does not exist");
            }
            if (studentRepository.existsById(body.getStudentId()) == false) {
                throw new IllegalArgumentException("Student does not exist");
            }

            TestResultEntity testResultEntity = modelMapperService.mapClass(body, TestResultEntity.class);
            TestEntity testEntity = testRepository.findById(body.getTestId()).orElseThrow(() -> new IllegalArgumentException("Test does not exist"));
            int count = testResultRepository.countByStudentIdAndTestId(body.getStudentId(), body.getTestId());
            if (testEntity.getAttemptLimit()==null){
                testEntity.setAttemptLimit(1);
            }

            TestResultEntity ongoingTest = testResultRepository.findFirstByStudentIdAndTestIdAndStateOrderByAttendedAtDesc(body.getStudentId(),body.getTestId(), TestState.ONGOING.name());
            if (ongoingTest != null){
                if (System.currentTimeMillis() - Long.parseLong(ongoingTest.getAttendedAt()) < testEntity.getDuration()  * 1000) {
                    throw new IllegalArgumentException("You have an ongoing test");
                }
            }
            if (count >= testEntity.getAttemptLimit()) {
                throw new IllegalArgumentException("You have reached the limit of attempts");
            }
            testResultEntity.setState(TestState.ONGOING);
            testResultEntity.setAttendedAt(String.valueOf(System.currentTimeMillis()));
            testResultEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            testResultEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            testResultRepository.save(testResultEntity);

            StartTestResponse startTestResponse = new StartTestResponse();
            startTestResponse.setStudentId(body.getStudentId());
            startTestResponse.setTestId(body.getTestId());
            startTestResponse.setTestResultId(testResultEntity.getId());
            startTestResponse.setAttendedAt(testResultEntity.getAttendedAt());
            return startTestResponse;

        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public void updateTestResult(UpdateTestResultRequest body) {
        try{
            if (body.getGrade() < 0 || body.getGrade() > 100) {
                throw new IllegalArgumentException("Grade must be between 0 and 100");
            }
            TestResultEntity testResultEntity = testResultRepository.findById(body.getId()).orElseThrow(() -> new IllegalArgumentException("Test result does not exist"));
            if (body.getGrade() != 0 && body.getGrade() != testResultEntity.getGrade()) {
                testResultEntity.setGrade(body.getGrade());
            }
            if (body.isPassed() != testResultEntity.getIsPassed()) {
                testResultEntity.setIsPassed(body.isPassed());
            }

            testResultRepository.save(testResultEntity);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }

    }

    @Override
    public void deleteTestResult(String studentId, String courseId) {

        try{
//            TestResultEntity testResultEntity = testResultRepository.findByStudentIdAndTestId(studentId, courseId);
//            testResultRepository.delete(testResultEntity);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public void saveProgress(SaveProgressRequest body) {
        try {
            TestResultEntity testResultEntity = testResultRepository.findById(body.getTestResultId())
                    .orElseThrow(() -> new IllegalArgumentException("Test result does not exist"));
            if (testResultEntity.getState() == TestState.FINISHED) {
                throw new IllegalArgumentException("Test is already finished");
            }
            studentAnswerRepository.deleteByTestResultId(body.getTestResultId());
            for (SaveProgressRequest.QuestionAndAnswer questionAndAnswer : body.getQuestionAndAnswers()) {
                QuestionEntity questionEntity = questionRepository.findById(questionAndAnswer.getQuestionId())
                        .orElseThrow(() -> new IllegalArgumentException("Question does not exist"));

                if (questionEntity.getType().equals(QuestionType.TEXT_ANSWER) || questionEntity.getType().equals(QuestionType.FILL_IN_THE_BLANK)) {
                    for (String text : questionAndAnswer.getTextAnswers()) {
                        StudentAnswersEntity studentAnswersEntity = studentAnswerRepository
                                .findByStudentIdAndTestResultIdAndQuestionIdAndAnswerId(testResultEntity.getStudentId(), body.getTestResultId(), questionAndAnswer.getQuestionId(), null);
                        studentAnswersEntity.setQuestionId(questionAndAnswer.getQuestionId());
                        studentAnswersEntity.setTestResultId(body.getTestResultId());
                        studentAnswersEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                        studentAnswersEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                        studentAnswersEntity.setStudentId(testResultEntity.getStudentId());
                        studentAnswersEntity.setTextAnswer(text);
                        studentAnswerRepository.save(studentAnswersEntity);
                    }
                } else {
                    for (String answerId : questionAndAnswer.getAnswers()) {
                        StudentAnswersEntity studentAnswersEntity = studentAnswerRepository
                                .findByStudentIdAndTestResultIdAndQuestionIdAndAnswerId(testResultEntity.getStudentId(), body.getTestResultId(), questionAndAnswer.getQuestionId(), answerId);
                        studentAnswersEntity.setAnswerId(answerId);
                        studentAnswersEntity.setQuestionId(questionAndAnswer.getQuestionId());
                        studentAnswersEntity.setTestResultId(body.getTestResultId());
                        studentAnswersEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                        studentAnswersEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
                        studentAnswersEntity.setStudentId(testResultEntity.getStudentId());
                        studentAnswerRepository.save(studentAnswersEntity);
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    @Override
    public List<TestResultsForClassroomResponse> getTestResultsForClassroom(String classroomId) {
        try{
            if (classroomId == null) {
                throw new IllegalArgumentException("Classroom id must be provided");
            }
            if (classRoomRepository.existsById(classroomId) == false) {
                throw new IllegalArgumentException("Classroom does not exist");
            }
            return studentEnrollmentsRepository.getTestResultsForClassroom(classroomId);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }
    }
    @Override
    public List<TestResultForStudentResponse> getTestResultsByStudentIdAndClassroomId(String studentId, String classroomId) {
        try {
            if (studentId == null || classroomId == null) {
                throw new IllegalArgumentException("Student id and classroom id must be provided");
            }
            if (studentRepository.existsById(studentId) == false) {
                throw new IllegalArgumentException("Student does not exist");
            }
            if (classRoomRepository.existsById(classroomId) == false) {
                throw new IllegalArgumentException("Classroom does not exist");
            }
            return studentEnrollmentsRepository.getTestResultsForStudent(studentId, classroomId);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());

        }
    }

    @Override
    public OverviewResultResponse getOverviewOfTestResults(String testId) {
        validateTestIdf(testId);
        List<TestResultOfTestResponse> results = testResultRepository.findHighestGradesByTestIdAndFinishedStateSortedAscending(testId);

        if (results.isEmpty()) {
            OverviewResultResponse response = new OverviewResultResponse();
            response.setTotalStudent(0);
            response.setTotalPassed(0);
            response.setTotalFailed(0);
            response.setTotalNotAttended(0);
            response.setTotalGrade(0);
            response.setMaxGrade(0);
            response.setMinGrade(0);
            response.setAverageGrade(0);
            return response;
        }
        int totalStudentInClass = getTotalStudentsInClass(results.get(0).getTestInfo().getClassroomId());
        OverviewResultResponse response = new OverviewResultResponse();

        response.setTotalStudent(totalStudentInClass);


        calculateAndSetStatistics(response, results, totalStudentInClass);
        return response;
    }

    private void validateTestIdf(String testId) {
        if (testId == null) {
            throw new IllegalArgumentException("Test id must be provided");
        }
        if (!testRepository.existsById(testId)) {
            throw new IllegalArgumentException("Test does not exist");
        }
    }

    private int getTotalStudentsInClass(String classroomId) {
        return studentEnrollmentsRepository.countByClassroomId(classroomId);
    }

    private void calculateAndSetStatistics(OverviewResultResponse response, List<TestResultOfTestResponse> results, int totalStudentInClass) {
        int totalPassed = 0;
        int totalFailed = 0;
        double totalGrade = 0;
        int maxGrade = Integer.MIN_VALUE;
        int minGrade = Integer.MAX_VALUE;

        for (TestResultOfTestResponse result : results) {
            if (result.getIsPassed()) {
                totalPassed++;
            } else {
                totalFailed++;
            }
            totalGrade += result.getGrade();
            maxGrade = Math.max(maxGrade, result.getGrade());
            minGrade = Math.min(minGrade, result.getGrade());
        }

        int resultCount = results.size();
        double averageGrade = resultCount > 0 ? totalGrade / resultCount : 0;

        response.setMaxGrade(maxGrade);
        response.setMinGrade(minGrade);
        response.setAverageGrade(averageGrade);
        response.setTotalPassed(totalPassed);
        response.setTotalFailed(totalFailed);
        response.setTotalNotAttended(totalStudentInClass - totalPassed - totalFailed);
        response.setTotalGrade(resultCount);
    }

    @Override
    public StatisticsResultResponse getStatisticsQuestionAndAnswerOfTest(String testId) {
        validateTestId(testId);

        GetTestDetailResponse testDetail = testService.getTestDetail(testId);
        OverviewResultResponse overviewResult = getOverviewOfTestResults(testId);
        List<TestResultOfTestResponse> results = testResultRepository.findHighestGradesByTestIdAndFinishedStateSortedAscending(testId);

        StatisticsResultResponse response = new StatisticsResultResponse();
        List<StatisticsResultResponse.Question> processedQuestions = processQuestions(testDetail.getQuestions(), results,testDetail.getId(),testDetail.getClassroomId());
        response.setQuestionSortByIncorrectRate(sortQuestionsByIncorrectRate(processedQuestions));
        setOverviewStatistics(response, overviewResult);

        return response;
    }

    @Override
    public List<StudentEntity> getStudentNotAttemptedTest(String testId) {
        validateTestId(testId);
        TestEntity test = testRepository.findById(testId).orElseThrow(() -> new IllegalArgumentException("Test does not exist"));
        List<String> studentIds = studentEnrollmentsRepository.findStudentsNotTakenTest(test.getClassroomId(),testId);
        List<StudentEntity> data  =  new ArrayList<>();
        for (String studentId : studentIds) {
            StudentEntity student = studentRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student does not exist"));
            data.add(student);
        }
        return data;
    }
    @Override
    public ScoreDistributionResponse getScoreDistributionOfTest(String testId, String fullname, Integer minGrade, Integer maxGrade, Boolean passed, int page, int size, String sortBy, String sortOrder) {
        validateTestId(testId);

        List<TestResultOfTestResponse> allResults = testResultRepository.findHighestGradesByTestIdAndFilters(
                testId, fullname, minGrade, maxGrade, passed);

        List<ScoreDistributionResponse.ScoreDistribution> data = allResults.stream()
                .map(this::mapToScoreDistributionResponse)
                .collect(Collectors.toList());

        // Apply sorting
        data.sort((d1, d2) -> {
            switch (sortBy.toLowerCase()) {
                case "fullname":
                    return sortOrder.equalsIgnoreCase("desc") ?
                            d2.getFullname().compareTo(d1.getFullname()) :
                            d1.getFullname().compareTo(d2.getFullname());
                case "grade":
                    return sortOrder.equalsIgnoreCase("desc") ?
                            Double.compare(d2.getGrade(), d1.getGrade()) :
                            Double.compare(d1.getGrade(), d2.getGrade());
                case "totalcorrect":
                    return sortOrder.equalsIgnoreCase("desc") ?
                            Integer.compare(d2.getTotalCorrect(), d1.getTotalCorrect()) :
                            Integer.compare(d1.getTotalCorrect(), d2.getTotalCorrect());
                case "totalincorrect":
                    return sortOrder.equalsIgnoreCase("desc") ?
                            Integer.compare(d2.getTotalIncorrect(), d1.getTotalIncorrect()) :
                            Integer.compare(d1.getTotalIncorrect(), d2.getTotalIncorrect());
                case "totalattempted":
                    return sortOrder.equalsIgnoreCase("desc") ?
                            Integer.compare(d2.getTotalAttempted(), d1.getTotalAttempted()) :
                            Integer.compare(d1.getTotalAttempted(), d2.getTotalAttempted());
                default:
                    return 0;
            }
        });

        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, data.size());
        List<ScoreDistributionResponse.ScoreDistribution> pageContent;
        if (start >= data.size()) {
            pageContent = Collections.emptyList();
        } else {
            pageContent = data.subList(start, end);
        }
        ScoreDistributionResponse response = new ScoreDistributionResponse();
        response.setScoreDistributions(pageContent);
        response.setTotalPage((int) Math.ceil((double) data.size() / size));
        response.setTotalElements((long) data.size());
        return response;
    }
    @Override
    public GetQuestionChoiceRateResponse getQuestionChoiceRate(String testId, String questionContent, Integer minCorrectCount, Integer maxCorrectCount, int page, int size, String sortBy, String sortOrder) {
        validateTestId(testId);

        GetTestDetailResponse testDetail = testService.getTestDetail(testId);
        List<TestResultOfTestResponse> results = testResultRepository.findHighestGradesByTestIdAndFinishedStateSortedAscending(testId);

        List<StatisticsResultResponse.Question> processedQuestions = processQuestions(testDetail.getQuestions(), results,testDetail.getId(),testDetail.getClassroomId());

        List<StatisticsResultResponse.Question> filteredQuestions = filterQuestions(processedQuestions, questionContent, minCorrectCount, maxCorrectCount);

        // Apply sorting
        filteredQuestions.sort((q1, q2) -> {
            switch (sortBy.toLowerCase()) {
                case "totalcorrect":
                    return sortOrder.equalsIgnoreCase("desc") ?
                            Integer.compare(q2.getTotalCorrect(), q1.getTotalCorrect()) :
                            Integer.compare(q1.getTotalCorrect(), q2.getTotalCorrect());
                case "totalincorrect":
                    return sortOrder.equalsIgnoreCase("desc") ?
                            Integer.compare(q2.getTotalIncorrect(), q1.getTotalIncorrect()) :
                            Integer.compare(q1.getTotalIncorrect(), q2.getTotalIncorrect());
                case "content":
                    return sortOrder.equalsIgnoreCase("desc") ?
                            q2.getContent().compareTo(q1.getContent()) :
                            q1.getContent().compareTo(q2.getContent());
                default:
                    return 0;
            }
        });

        // Apply pagination
        List<StatisticsResultResponse.Question> paginatedQuestions = paginateQuestions(filteredQuestions, page, size);

        GetQuestionChoiceRateResponse response = new GetQuestionChoiceRateResponse();
        response.setQuestions(paginatedQuestions);
        response.setTotalElements((long)filteredQuestions.size());
        response.setTotalPage((int) Math.ceil((double) filteredQuestions.size() / size));
        return response;
    }

    private List<StatisticsResultResponse.Question> filterQuestions(List<StatisticsResultResponse.Question> questions, String questionContent, Integer minCorrectCount, Integer maxCorrectCount) {
        return questions.stream()
                .filter(q -> questionContent == null || q.getContent().toLowerCase().contains(questionContent.toLowerCase()))
                .filter(q -> minCorrectCount == null || q.getTotalCorrect() >= minCorrectCount)
                .filter(q -> maxCorrectCount == null || q.getTotalCorrect() <= maxCorrectCount)
                .collect(Collectors.toList());
    }

    private List<StatisticsResultResponse.Question> paginateQuestions(List<StatisticsResultResponse.Question> questions, int page, int size) {
        if (page==-1){
            page=0;
        }
        int fromIndex = (page ) * size;
        if (fromIndex >= questions.size()) {
            return Collections.emptyList();
        }
        return questions.subList(fromIndex, Math.min(fromIndex + size, questions.size()));
    }

    private ScoreDistributionResponse.ScoreDistribution mapToScoreDistributionResponse(TestResultOfTestResponse result) {
        int totalCorrect = studentAnswerRepository.countCorrectAnswersByTestResultId(result.getResultId());
        int totalQuestion = questionRepository.countByTestId(result.getTestId());
        int totalAttempted = testResultRepository.countByStudentIdAndTestId(result.getStudentId(), result.getTestId());
        TestEntity test = testRepository.findById(result.getTestId()).orElseThrow(() -> new IllegalArgumentException("Test does not exist"));
        ScoreDistributionResponse.ScoreDistribution response = new ScoreDistributionResponse.ScoreDistribution();
        StudentEntity student = studentRepository.findById(result.getStudentId()).orElseThrow(() -> new IllegalArgumentException("Student does not exist"));
        response.setStudentId(student.getId());
        response.setFullname(student.getUser().getFullname());
        response.setEmail(student.getUser().getEmail());
        response.setPhone(student.getPhone());
        response.setTotalCorrect(totalCorrect);
        response.setTotalIncorrect(totalQuestion - totalCorrect);
        response.setTotalAttempted(totalAttempted);
        response.setIsPassed(result.getIsPassed());
        response.setGrade(result.getGrade());
        response.setAttemptLimit(test.getAttemptLimit());
        return response;
    }

    private void validateTestId(String testId) {
        if (testId == null) {
            throw new IllegalArgumentException("Test id must be provided");
        }
        if (!testRepository.existsById(testId)) {
            throw new IllegalArgumentException("Test does not exist");
        }
    }

    private List<StatisticsResultResponse.Question> processQuestions(List<GetQuestionsResponse.QuestionResponse> questions, List<TestResultOfTestResponse> results,String testId,String classroomId) {
        return questions.stream()
                .map(question -> processQuestion(question, results,testId,classroomId))
                .collect(Collectors.toList());
    }

    private StatisticsResultResponse.Question processQuestion(GetQuestionsResponse.QuestionResponse question, List<TestResultOfTestResponse> results, String testId, String classroomId) {
        StatisticsResultResponse.Question questionRes = modelMapperService.mapClass(question, StatisticsResultResponse.Question.class);
        questionRes.setAnswers(processAnswers(question.getAnswers(), results));
        questionRes.setSources(fileRepository.findByOwnerIdAndOwnerType(question.getId(), FileOwnerType.QUESTION.name()));
        List<String> studentIds = studentEnrollmentsRepository.findStudentsTakenTest(classroomId, testId);
        questionRes.setTotalCorrect(0);
        questionRes.setTotalIncorrect(0);

        for (String studentId : studentIds) {
            TestResultEntity testResultEntity = testResultRepository.findHighestGradeByStudentIdAndTestId(studentId, testId);
            List<StudentAnswersEntity> studentAnswersEntities = studentAnswerRepository.findByStudentIdAndTestResultIdAndQuestionId(studentId, testResultEntity.getId(), question.getId());

            boolean isCorrect;
            if (questionRes.getType().equals(QuestionType.TEXT_ANSWER.name()) || questionRes.getType().equals(QuestionType.FILL_IN_THE_BLANK.name())) {
                isCorrect = questionRes.getAnswers().stream()
                        .allMatch(answer -> studentAnswersEntities.stream()
                                .anyMatch(studentAnswer -> studentAnswer.getTextAnswer().equals(answer.getContent())));
            } else {
                long correctAnswersCount = questionRes.getAnswers().stream().filter(StatisticsResultResponse.Answers::getIsCorrect).count();
                long studentCorrectAnswersCount = studentAnswersEntities.stream().filter(studentAnswer -> {
                    AnswerEntity answer = answerRepository.findById(studentAnswer.getAnswerId()).orElse(null);
                    return answer != null && answer.getIsCorrect();
                }).count();
                isCorrect = correctAnswersCount == studentCorrectAnswersCount;
            }

            if (isCorrect) {
                questionRes.setTotalCorrect(questionRes.getTotalCorrect() + 1);
            } else {
                questionRes.setTotalIncorrect(questionRes.getTotalIncorrect() + 1);
            }
        }

        return questionRes;
    }

    private List<StatisticsResultResponse.Answers> processAnswers(List<GetQuestionsResponse.AnswerResponse> answers, List<TestResultOfTestResponse> results) {
        return answers.stream()
                .map(answer -> processAnswer(answer, results))
                .collect(Collectors.toList());
    }

    private StatisticsResultResponse.Answers processAnswer(GetQuestionsResponse.AnswerResponse answer, List<TestResultOfTestResponse> results) {
        StatisticsResultResponse.Answers answerRes = modelMapperService.mapClass(answer, StatisticsResultResponse.Answers.class);
        int count = countStudentAnswers(answer.getId(), results);
        AnswerEntity answerEntity = answerRepository.findById(answer.getId()).orElse(null);
        answerRes.setIsCorrect(answerEntity!=null?answerEntity.getIsCorrect():null);
        answerRes.setTotalSelected(count);
        return answerRes;
    }

    private int countStudentAnswers(String answerId, List<TestResultOfTestResponse> results) {
        return (int) results.stream()
                .filter(result -> studentAnswerRepository.countByStudentIdAndTestResultIdAndAnswerId(result.getStudentId(), result.getResultId(), answerId) > 0)
                .count();
    }

    private int[] calculateTotals(List<StatisticsResultResponse.Answers> answers) {
        int totalCorrect = 0;
        int totalIncorrect = 0;
        for (StatisticsResultResponse.Answers answer : answers) {

            if (answer.getIsCorrect()!=null)
            {
                if (answer.getIsCorrect()) {
                    totalCorrect += answer.getTotalSelected();
                } else {
                    totalIncorrect += answer.getTotalSelected();
                }
            }else{
                totalIncorrect += answer.getTotalSelected();
            }

        }
        return new int[]{totalCorrect, totalIncorrect};
    }

    private void setOverviewStatistics(StatisticsResultResponse response, OverviewResultResponse overviewResult) {
        response.setTotalPassed(overviewResult.getTotalPassed());
        response.setTotalFailed(overviewResult.getTotalFailed());
        response.setTotalAttempted(overviewResult.getTotalGrade());
        response.setTotalNotAttempted(overviewResult.getTotalStudent() - overviewResult.getTotalGrade());
    }
    private List<StatisticsResultResponse.Question> sortQuestionsByIncorrectRate(List<StatisticsResultResponse.Question> questions) {
        return questions.stream()
                .sorted((q1, q2) -> {
                    double rate1 = calculateIncorrectRate(q1);
                    double rate2 = calculateIncorrectRate(q2);
                    return Double.compare(rate2, rate1); // Sắp xếp giảm dần
                })
                .limit(5) // Giới hạn kết quả chỉ còn 5 phần tử
                .collect(Collectors.toList());
    }

    private double calculateIncorrectRate(StatisticsResultResponse.Question question) {
        int total = question.getTotalCorrect() + question.getTotalIncorrect();
        return total == 0 ? 0 : (double) question.getTotalIncorrect() / total;
    }

}
