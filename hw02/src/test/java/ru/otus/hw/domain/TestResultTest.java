package ru.otus.hw.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class TestResultTest {

    @DisplayName("getAnsweredQuestions() should be equal to number of applied answers")
    @Test
    void shouldEqualToNumberOfAppliedAnswers() {
        Student student = new Student("John", "Doe");
        List<Question> questions = List.of(
                new Question("Question 1?", List.of()),
                new Question("Question 2?", List.of()),
                new Question("Question 3?", List.of())
        );
        TestResult testResult = new TestResult(student);
        Random random = new Random();
        questions.forEach(question -> testResult.applyAnswer(question, random.nextBoolean()));
        assertThat(testResult.getAnsweredQuestions()).containsExactlyInAnyOrderElementsOf(questions);
    }

    @DisplayName("getRightAnswersCount() should be equal to number of questions after applying all answers correctly")
    @Test
    void shouldEqualToNumberOfQuestionsWhenAllAnswersAreCorrect() {
        Student student = new Student("John", "Doe");
        List<Question> questions = List.of(
                new Question("Correctly answered question 1?", List.of()),
                new Question("Correctly answered question 2?", List.of()),
                new Question("Correctly answered question 3?", List.of())
        );
        TestResult testResult = new TestResult(student);
        questions.forEach(question -> testResult.applyAnswer(question, true));
        assertThat(testResult.getRightAnswersCount()).isEqualTo(questions.size());
        assertThat(testResult.getAnsweredQuestions()).containsExactlyInAnyOrderElementsOf(questions);
    }

    @DisplayName("getRightAnswersCount() should be equal to number of correctly answered questions")
    @Test
    void shouldEqualToNumberOffCorrectlyAnsweredQuestions() {
        Student student = new Student("John", "Doe");
        Question correct1 = new Question("Correctly answered question 1?", List.of());
        Question correct2 = new Question("Correctly answered question 2?", List.of());
        Question correct3 = new Question("Correctly answered question 3?", List.of());
        Question incorrect = new Question("Incorrectly answered question 4?", List.of());
        TestResult testResult = new TestResult(student);
        testResult.applyAnswer(correct1, true);
        testResult.applyAnswer(correct2, true);
        testResult.applyAnswer(correct3, true);
        testResult.applyAnswer(incorrect, false);
        assertThat(testResult.getRightAnswersCount()).isEqualTo(3);
        assertThat(testResult.getAnsweredQuestions()).containsExactlyInAnyOrder(correct1, correct2, correct3, incorrect);
    }

    @DisplayName("getRightAnswersCount() should be equal to zero when there is no correct answers")
    @Test
    void shouldEqualToZeroWithNoCorrectAnswers() {
        Student student = new Student("John", "Doe");
        List<Question> questions = List.of(
                new Question("Incorrectly answered question 1?", List.of()),
                new Question("Incorrectly answered question 2?", List.of()),
                new Question("Incorrectly answered question 3?", List.of())
        );
        TestResult testResult = new TestResult(student);
        questions.forEach(question -> testResult.applyAnswer(question, false));
        assertThat(testResult.getRightAnswersCount()).isEqualTo(0);
        assertThat(testResult.getAnsweredQuestions()).containsExactlyInAnyOrderElementsOf(questions);
    }
}
