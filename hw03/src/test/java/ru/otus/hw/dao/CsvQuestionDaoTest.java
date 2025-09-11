package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

    private static final String CSV_FILE = "questions.csv";

    @Mock
    TestFileNameProvider fileNameProvider;

    @InjectMocks
    CsvQuestionDao questionDao;

    @DisplayName("findAll() should return list of all questions with their corresponding answers from CSV file")
    @Test
    void shouldReturnListOfAllQuestionsWithAnswersFromCsvFile() {
        given(fileNameProvider.getTestFileName()).willReturn(CSV_FILE);
        List<Question> questions = questionDao.findAll();
        assertThat(questions).hasSize(3);
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            assertThat(question.text()).contains("Random Question " + (i + 1) + "?");

            List<Answer> answers = question.answers();
            assertThat(answers).isNotEmpty();

            Answer correctAnswer = answers.stream().filter(Answer::isCorrect).findFirst().orElse(null);
            assertThat(correctAnswer).isNotNull();
        }
    }

    @DisplayName("findAll() should throw QuestionReadException when CSV file with questions does not exist")
    @Test
    void shouldThrowExceptionWhenFileDoesNotExist() {
        given(fileNameProvider.getTestFileName()).willReturn("non_existent_file.csv");
        assertThatThrownBy(() -> questionDao.findAll())
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("not found");
    }
}
