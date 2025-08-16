package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @Test
    void shouldOutputAllQuestionsWithCorrespondingAnswers() {
        var questions = getQuestions();
        given(questionDao.findAll()).willReturn(questions);
        testService.executeTest();

        verify(ioService, times(3)).printLine("");

        for (int i = 0; i < questions.size(); ++i) {
            var question = questions.get(i);

            verify(ioService).printFormattedLine("Question %d: %s", i + 1, question.text());

            var answers = question.answers();
            for (int j = 0; j < answers.size(); ++j) {
                var answer = answers.get(j);

                verify(ioService).printFormattedLine("%d) %s", j + 1, answer.text());
            }
        }
    }

    private static List<Question> getQuestions() {
        var question1 = "Some random question 1?";
        var answer11 = "Some random answer 11";
        var answer12 = "Some random answer 12";

        var question2 = "Some random question 2?";
        var answer21 = "Some random answer 21";
        var answer22 = "Some random answer 22";
        var answer23 = "Some random answer 23";

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
