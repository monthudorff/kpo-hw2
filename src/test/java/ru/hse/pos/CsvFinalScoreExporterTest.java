package ru.hse.pos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.hse.pos.application.ExportException;
import ru.hse.pos.domain.FinalScore;
import ru.hse.pos.domain.Student;
import ru.hse.pos.infrastructure.CsvFinalScoreExporter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvFinalScoreExporterTest {
    @TempDir
    Path directory;

    @Test
    void writesUtf8CsvAndEscapesSpecialCharacters() throws IOException {
        Path destination = directory.resolve("итоги.csv");
        var scores = List.of(new FinalScore(new Student(1, "Анна, \"А\"\nИванова"), 6.25));

        new CsvFinalScoreExporter().export(scores, destination.toString());

        assertEquals("student_id,student_name,final_score\r\n1,\"Анна, \"\"А\"\"\nИванова\",6.25\r\n",
                Files.readString(destination, StandardCharsets.UTF_8));
    }

    @Test
    void fileErrorBecomesUnderstandableApplicationError() {
        String destination = directory.resolve("missing/results.csv").toString();

        ExportException error = assertThrows(ExportException.class,
                () -> new CsvFinalScoreExporter().export(List.of(), destination));

        assertTrue(error.getMessage().contains("Не удалось записать CSV"));
        assertNotNull(error.getCause());
    }

    @Test
    void invalidPathBecomesApplicationError() {
        assertThrows(ExportException.class,
                () -> new CsvFinalScoreExporter().export(List.of(), "bad\u0000.csv"));
    }
}
