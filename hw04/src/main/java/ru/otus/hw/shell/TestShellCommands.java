package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent
@RequiredArgsConstructor
public class TestShellCommands {

    private final TestRunnerService testRunnerService;

    private final LocalizedIOService ioService;

    @ShellMethod(value = "Start testing of a student", key = {"s", "start"})
    public void start() {
        ioService.printLineLocalized("ShellCommands.test.start");
        testRunnerService.run();
    }
}