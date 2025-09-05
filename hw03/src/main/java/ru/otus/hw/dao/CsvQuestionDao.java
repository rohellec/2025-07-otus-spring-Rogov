package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {

    private static final char CSV_SEPARATOR = ';';

    private static final char SKIP_LINES_COUNT = 1;

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        var fileName = fileNameProvider.getTestFileName();
        var inputStream = getFileFromResourceAsStream(fileName);
        var reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            var csvToBean = buildCsvToBeanParser(reader);
            return csvToBean.parse().stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (RuntimeException ex) {
            throw new QuestionReadException("Error during parsing questions csv file " + fileName, ex);
        }
    }

    private CsvToBean<QuestionDto> buildCsvToBeanParser(Reader reader) {
        return new CsvToBeanBuilder<QuestionDto>(reader)
                .withType(QuestionDto.class)
                .withSeparator(CSV_SEPARATOR)
                .withSkipLines(SKIP_LINES_COUNT)
                .build();
    }

    private InputStream getFileFromResourceAsStream(String fileName) {
        var classLoader = getClass().getClassLoader();
        var inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new QuestionReadException("Questions file not found! " + fileName);
        }
        return inputStream;
    }
}
