package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@EnableConfigurationProperties(AppProperties.class)
@SpringBootTest(classes = ResultServiceImpl.class)
public class ResultServiceImplTest {

    @MockitoBean
    private LocalizedIOService ioService;

    @Autowired
    private ResultServiceImpl resultService;

    @DisplayName("showResult() should print student's localized test results information")
    @Test
    void shouldPrintTestResultsForStudent() {
        TestResult testResult = prepareTestResultWithPassedAnswersCount();
        resultService.showResult(testResult);
        verify(ioService).printLineLocalized("ResultService.test.results");
        verify(ioService).printFormattedLineLocalized("ResultService.student", testResult.getStudent().getFullName());
        verify(ioService).printFormattedLineLocalized("ResultService.answered.questions.count", testResult.getAnsweredQuestions().size());
        verify(ioService).printFormattedLineLocalized("ResultService.right.answers.count", testResult.getRightAnswersCount());
    }

    @DisplayName("showResult() should output localized 'Passed' test result when all answers are correct")
    @Test
    void shouldOutputPassedTestResultWhenAllAnswersAreCorrect() {
        TestResult testResult = prepareTestResultWithAllCorrectAnswers();
        resultService.showResult(testResult);
        verify(ioService).printLineLocalized("ResultService.passed.test");
    }

    @DisplayName("showResult() should output localized 'Passed' test result when the number of correct answers matches configuration property")
    @Test
    void shouldOutputPassedTestResultWhenNumberOfCorrectAnswersMatchesProperty() {
        TestResult testResult = prepareTestResultWithPassedAnswersCount();
        resultService.showResult(testResult);
        verify(ioService).printLineLocalized("ResultService.passed.test");
    }

    @DisplayName("showResult() should output localized 'Failed' test result when the number of correct answers is below the configuration property")
    @Test
    void shouldOutputFailedTestResultWhenNumberOfCorrectAnswersIsBelowProperty() {
        TestResult testResult = prepareTestResultWithFailedAnswersCount();
        resultService.showResult(testResult);
        verify(ioService).printLineLocalized("ResultService.fail.test");
    }

    @DisplayName("showResult() should output localized 'Failed' test result when all answers are incorrect")
    @Test
    void shouldOutputFailedTestResultWhenAllAnswersAreIncorrect() {
        TestResult testResult = prepareTestResultWithNoCorrectAnswers();
        resultService.showResult(testResult);
        verify(ioService).printLineLocalized("ResultService.fail.test");
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
