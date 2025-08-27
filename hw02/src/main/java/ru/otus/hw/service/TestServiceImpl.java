package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
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
            ioService.printFormattedLine("Question %d: %s", i + 1, question.text());
            var answers = question.answers();

            for (int j = 0; j < answers.size(); ++j) {
                var answer = answers.get(j);
                ioService.printFormattedLine("%d) %s", j + 1, answer.text());
            }

            var appliedAnswer = ioService.readIntForRangeWithPrompt(1, answers.size(),
                    "Please enter the number of correct answer",
                    "Incorrect input number!");
            ioService.printLine("");

            var isAnswerValid = answers.get(appliedAnswer - 1).isCorrect(); // Задать вопрос, получить ответ
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }
}
