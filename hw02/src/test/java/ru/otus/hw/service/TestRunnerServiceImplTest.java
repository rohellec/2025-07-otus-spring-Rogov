package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TestRunnerServiceImplTest {

    @Mock
    private TestService testService;

    @Mock
    private StudentService studentService;

    @Mock
    private ResultService resultService;

    @InjectMocks
    private TestRunnerServiceImpl testRunnerService;

    @DisplayName("run() should run student's test using other services")
    @Test
    public void shouldRunTestApplicationUsingOtherServices() {
        Student student = new Student("John", "Doe");
        given(studentService.determineCurrentStudent()).willReturn(student);

        TestResult testResult = new TestResult(student);
        given(testService.executeTestFor(student)).willReturn(testResult);

        InOrder inOrder = inOrder(studentService, testService, resultService);
        testRunnerService.run();
        inOrder.verify(studentService, times(1)).determineCurrentStudent();
        inOrder.verify(testService, times(1)).executeTestFor(student);
        inOrder.verify(resultService, times(1)).showResult(testResult);
        inOrder.verifyNoMoreInteractions();
    }
}
