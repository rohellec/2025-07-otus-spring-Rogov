package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (int i = 0; i < questions.size(); ++i) {
            var question = questions.get(i);
            printQuestionWithAnswers(question, i + 1);

            var answers = question.answers();
            var appliedAnswer = ioService.readIntForRangeWithPrompt(1, answers.size(),
                    "Please enter the number of correct answer",
                    "Incorrect input number!");
            ioService.printLine("");

            var isAnswerValid = answers.get(appliedAnswer - 1).isCorrect(); // Задать вопрос, получить ответ
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void printQuestionWithAnswers(Question question, int order) {
        ioService.printFormattedLine("Question %d: %s", order, question.text());
        var answers = question.answers();

        for (int i = 0; i < answers.size(); ++i) {
            var answer = answers.get(i);
            ioService.printFormattedLine("%d) %s", i + 1, answer.text());
        }
    }
}
