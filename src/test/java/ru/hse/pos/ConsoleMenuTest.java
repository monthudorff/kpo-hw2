package ru.hse.pos;

import org.junit.jupiter.api.Test;
import ru.hse.pos.application.ExportException;
import ru.hse.pos.application.GradebookApplication;
import ru.hse.pos.cli.ConsoleMenu;
import ru.hse.pos.domain.FinalScoreService;
import ru.hse.pos.domain.WeightedScorePolicy;
import ru.hse.pos.infrastructure.InMemoryGradebookRepository;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleMenuTest {
    @Test
    void invalidInputAndDomainErrorsReturnToMenu() {
        var application = application();
        String commands = "wrong\n1\nАнна\n3\nПроект\n10\n1\n5\nx\n"
                + "5\n1\n1\n11\n5\n1\n1\n7,5\n6\n1\n9\n0\n";

        String output = run(application, commands);

        assertTrue(output.contains("выберите пункт меню"));
        assertTrue(output.contains("ID должен быть целым числом"));
        assertTrue(output.contains("Балл должен быть в диапазоне"));
        assertTrue(output.contains("Проект: 7.5 / 10.0"));
        assertTrue(output.contains("итог: 7.50 / 10"));
        assertTrue(output.contains("До свидания!"));
    }

    @Test
    void exportFailureDoesNotStopApplication() {
        var application = application();

        String output = run(application, "10\nresults.csv\n2\n0\n");

        assertTrue(output.contains("Ошибка: Тестовая ошибка файла"));
        assertTrue(output.contains("Студентов пока нет."));
        assertTrue(output.contains("До свидания!"));
    }

    @Test
    void endOfInputDuringOperationExitsWithoutPartialMutation() {
        var application = application();

        String output = run(application, "3\nПроект\n10\n");

        assertTrue(output.contains("Ввод завершён."));
        assertTrue(application.criteria().isEmpty());
    }

    private GradebookApplication application() {
        return new GradebookApplication(new InMemoryGradebookRepository(),
                new FinalScoreService(new WeightedScorePolicy()), (scores, path) -> {
                    throw new ExportException("Тестовая ошибка файла", new java.io.IOException());
                });
    }

    private String run(GradebookApplication application, String commands) {
        StringWriter output = new StringWriter();
        new ConsoleMenu(application, new BufferedReader(new StringReader(commands)),
                new PrintWriter(output)).run();
        return output.toString();
    }
}
