package ru.hse.pos.infrastructure;

import ru.hse.pos.application.ExportException;
import ru.hse.pos.application.FinalScoreExporter;
import ru.hse.pos.domain.FinalScore;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public final class CsvFinalScoreExporter implements FinalScoreExporter {
    @Override
    public void export(List<FinalScore> scores, String destination) {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(destination), StandardCharsets.UTF_8)) {
            writer.write("student_id,student_name,final_score\r\n");
            for (FinalScore score : scores) {
                writer.write(score.student().id() + "," + quoted(score.student().name()) + ","
                        + String.format(Locale.ROOT, "%.2f", score.value()) + "\r\n");
            }
        } catch (IOException | InvalidPathException | SecurityException e) {
            throw new ExportException("Не удалось записать CSV. Проверьте путь и права доступа: "
                    + e.getMessage(), e);
        }
    }

    private String quoted(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
