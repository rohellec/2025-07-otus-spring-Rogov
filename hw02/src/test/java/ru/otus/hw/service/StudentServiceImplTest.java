package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.domain.Student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith({MockitoExtension.class})
public class StudentServiceImplTest {

    @Mock
    private IOService ioService;

    @InjectMocks
    private StudentServiceImpl studentService;

    @DisplayName("determineCurrentStudent() should read first and second name and return new Student")
    @Test
    void shouldReadNameAndReturnNewStudentWithExpectedName() {
        given(ioService.readStringWithPrompt("Please input your first name")).willReturn("John");
        given(ioService.readStringWithPrompt("Please input your last name")).willReturn("Doe");
        Student student = studentService.determineCurrentStudent();
        assertThat(student.firstName()).isEqualTo("John");
        assertThat(student.lastName()).isEqualTo("Doe");
        assertThat(student.getFullName()).isEqualTo("John Doe");
    }
}
