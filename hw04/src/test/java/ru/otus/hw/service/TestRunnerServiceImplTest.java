package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = TestRunnerServiceImpl.class)
public class TestRunnerServiceImplTest {

    @MockitoBean
    private TestService testService;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private ResultService resultService;

    @Autowired
    private TestRunnerServiceImpl testRunnerService;

    @DisplayName("run() should execute student's test app using other services")
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
