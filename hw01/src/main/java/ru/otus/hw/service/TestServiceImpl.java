package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        printQuestions(questions);
    }

    private void printQuestions(List<Question> questions) {
        for (int i = 0; i < questions.size(); ++i) {
            var question = questions.get(i);
            ioService.printFormattedLine("Question %d: %s", i + 1, question.text());
            printAnswers(question.answers());
            ioService.printLine("");
        }
    }

    private void printAnswers(List<Answer> answers) {
        for (int j = 0; j < answers.size(); ++j) {
            var answer = answers.get(j);
            ioService.printFormattedLine("%d) %s", j + 1, answer.text());
        }
    }
}
