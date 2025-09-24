package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.domain.Student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith({MockitoExtension.class})
@SpringBootTest(classes = {StudentServiceImpl.class})
public class StudentServiceImplTest {

    @MockitoBean
    private LocalizedIOService ioService;

    @Autowired
    private StudentServiceImpl studentService;

    @DisplayName("determineCurrentStudent() should read first and second name and return new Student")
    @Test
    void shouldReadNameAndReturnNewStudentWithExpectedName() {
        given(ioService.readStringWithPromptLocalized("StudentService.input.first.name")).willReturn("John");
        given(ioService.readStringWithPromptLocalized("StudentService.input.last.name")).willReturn("Doe");
        Student student = studentService.determineCurrentStudent();
        assertThat(student.firstName()).isEqualTo("John");
        assertThat(student.lastName()).isEqualTo("Doe");
        assertThat(student.getFullName()).isEqualTo("John Doe");
    }
}
