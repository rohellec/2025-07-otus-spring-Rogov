package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @BeforeEach
    void prepareIoServiceInputMocks() {
        var questions = getQuestions();
        given(questionDao.findAll()).willReturn(questions);
        for (var question : questions) {
            var answers = question.answers();
            var lastAnswer = answers.size();
            given(ioService.readIntForRangeWithPrompt(1, answers.size(),
                    "Please enter the number of correct answer",
                    "Incorrect input number!"))
                    .willReturn(lastAnswer);
        }
    }

    @DisplayName("executeTestFor() should print all questions with their corresponding answers")
    @Test
    void shouldOutputAllQuestionsWithCorrespondingAnswers() {
        var questions = getQuestions();
        var student = new Student("John", "Doe");
        testService.executeTestFor(student);

        verify(ioService).printFormattedLine("Please answer the questions below%n");

        for (int i = 0; i < questions.size(); ++i) {
            var question = questions.get(i);

            verify(ioService).printFormattedLine("Question %d: %s", i + 1, question.text());

            var answers = question.answers();
            for (int j = 0; j < answers.size(); ++j) {
                var answer = answers.get(j);
                verify(ioService).printFormattedLine("%d) %s", j + 1, answer.text());
            }
            verify(ioService).readIntForRangeWithPrompt(1, answers.size(),
                    "Please enter the number of correct answer",
                    "Incorrect input number!");
        }
        verify(ioService, times(3)).printLine("");
        verifyNoMoreInteractions(ioService);
    }

    @DisplayName("executeTestFor() should return expected test result for student")
    @Test
    void shouldReturnExpectedTestResult() {
        var questions = getQuestions();
        var student = new Student("John", "Doe");
        TestResult testResult = testService.executeTestFor(student);
        assertThat(testResult.getStudent()).isEqualTo(student);
        assertThat(testResult.getAnsweredQuestions()).hasSize(questions.size());
        assertThat(testResult.getRightAnswersCount()).isEqualTo(1);
    }

    private static List<Question> getQuestions() {
        var question1 = "Random question 1?";
        var answer11 = "Random answer 11";
        var answer12 = "Random answer 12";

        var question2 = "Random question 2?";
        var answer21 = "Random answer 21";
        var answer22 = "Random answer 22";
        var answer23 = "Random answer 23";

        var answers1 = List.of(
                new Answer(answer11, true),
                new Answer(answer12, false)
        );
        var answers2 = List.of(
                new Answer(answer21, false),
                new Answer(answer22, false),
                new Answer(answer23, true)
        );
        return List.of(
                new Question(question1, answers1),
                new Question(question2, answers2)
        );
    }

}
