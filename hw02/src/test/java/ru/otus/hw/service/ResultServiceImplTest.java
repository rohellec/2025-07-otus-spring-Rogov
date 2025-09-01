package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ResultServiceImplTest {

    private static final int RIGHT_ANSWERS_COUNT_TO_PASS = 3;

    @Mock
    private TestConfig testConfig;

    @Mock
    private IOService ioService;

    @InjectMocks
    private ResultServiceImpl resultService;

    @DisplayName("showResult() should print student's test results information")
    @Test
    void shouldPrintTestResultsForStudent() {
        TestResult testResult = prepareTestResultWithPassedAnswersCount();
        resultService.showResult(testResult);
        verify(ioService).printLine("Test results: ");
        verify(ioService).printFormattedLine("Student: %s", testResult.getStudent().getFullName());
        verify(ioService).printFormattedLine("Answered questions count: %d", testResult.getAnsweredQuestions().size());
        verify(ioService).printFormattedLine("Right answers count: %d", testResult.getRightAnswersCount());
    }

    @DisplayName("showResult() should output 'Passed' test result when all answers are correct")
    @Test
    void shouldOutputPassedTestResultWhenAllAnswersAreCorrect() {
        TestResult testResult = prepareTestResultWithAllCorrectAnswers();
        given(testConfig.getRightAnswersCountToPass()).willReturn(RIGHT_ANSWERS_COUNT_TO_PASS);
        resultService.showResult(testResult);
        verify(ioService).printLine("Congratulations! You passed test!");
    }

    @DisplayName("showResult() should output 'Passed' test result when the number of correct answers matches configuration property")
    @Test
    void shouldOutputPassedTestResultWhenNumberOfCorrectAnswersMatchesProperty() {
        TestResult testResult = prepareTestResultWithPassedAnswersCount();
        given(testConfig.getRightAnswersCountToPass()).willReturn(RIGHT_ANSWERS_COUNT_TO_PASS);
        resultService.showResult(testResult);
        verify(ioService).printLine("Congratulations! You passed test!");
    }

    @DisplayName("showResult() should output 'Failed' test result when the number of correct answers is below the configuration property")
    @Test
    void shouldOutputFailedTestResultWhenNumberOfCorrectAnswersIsBelowProperty() {
        TestResult testResult = prepareTestResultWithFailedAnswersCount();
        given(testConfig.getRightAnswersCountToPass()).willReturn(RIGHT_ANSWERS_COUNT_TO_PASS);
        resultService.showResult(testResult);
        verify(ioService).printLine("Sorry. You fail test.");
    }

    @DisplayName("showResult() should output 'Failed' test result when all answers are incorrect")
    @Test
    void shouldOutputFailedTestResultWhenAllAnswersAreIncorrect() {
        TestResult testResult = prepareTestResultWithNoCorrectAnswers();
        given(testConfig.getRightAnswersCountToPass()).willReturn(RIGHT_ANSWERS_COUNT_TO_PASS);
        resultService.showResult(testResult);
        verify(ioService).printLine("Sorry. You fail test.");
    }

    private static TestResult prepareTestResultWithAllCorrectAnswers() {
        Student student = new Student("John", "Doe");
        List<Question> questions = List.of(
                new Question("Correctly answered question 1?", List.of()),
                new Question("Correctly answered question 2?", List.of()),
                new Question("Correctly answered question 3?", List.of())
        );
        TestResult testResult = new TestResult(student);
        questions.forEach(question -> testResult.applyAnswer(question, true));
        return testResult;
    }

    private static TestResult prepareTestResultWithPassedAnswersCount() {
        Student student = new Student("John", "Doe");
        Question correct1 = new Question("Correctly answered question 1?", List.of());
        Question correct2 = new Question("Correctly answered question 2?", List.of());
        Question correct3 = new Question("Correctly answered question 3?", List.of());
        Question incorrect4 = new Question("Incorrectly answered question 4?", List.of());
        TestResult testResult = new TestResult(student);
        testResult.applyAnswer(correct1, true);
        testResult.applyAnswer(correct2, true);
        testResult.applyAnswer(correct3, true);
        testResult.applyAnswer(incorrect4, false);
        return testResult;
    }

    private static TestResult prepareTestResultWithFailedAnswersCount() {
        Student student = new Student("John", "Doe");
        Question correct1 = new Question("Correctly answered question 1?", List.of());
        Question correct2 = new Question("Correctly answered question 2?", List.of());
        Question incorrect3 = new Question("Incorrectly answered question 3?", List.of());
        Question incorrect4 = new Question("Incorrectly answered question 4?", List.of());
        TestResult testResult = new TestResult(student);
        testResult.applyAnswer(correct1, true);
        testResult.applyAnswer(correct2, true);
        testResult.applyAnswer(incorrect3, false);
        testResult.applyAnswer(incorrect4, false);
        return testResult;
    }

    private static TestResult prepareTestResultWithNoCorrectAnswers() {
        Student student = new Student("John", "Doe");
        List<Question> questions = List.of(
                new Question("Incorrectly answered question 1?", List.of()),
                new Question("Incorrectly answered question 2?", List.of()),
                new Question("Incorrectly answered question 3?", List.of())
        );
        TestResult testResult = new TestResult(student);
        questions.forEach(question -> testResult.applyAnswer(question, false));
        return testResult;
    }
}
