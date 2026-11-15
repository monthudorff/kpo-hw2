package ru.hse.pos;

import org.junit.jupiter.api.Test;
import ru.hse.pos.application.GradebookApplication;
import ru.hse.pos.domain.FinalScore;
import ru.hse.pos.domain.FinalScoreService;
import ru.hse.pos.domain.WeightedScorePolicy;
import ru.hse.pos.infrastructure.InMemoryGradebookRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class GradebookApplicationTest {
    @Test
    void exportRecalculatesScoresAfterChanges() {
        AtomicReference<List<FinalScore>> exported = new AtomicReference<>();
        AtomicReference<String> destination = new AtomicReference<>();
        var application = new GradebookApplication(new InMemoryGradebookRepository(),
                new FinalScoreService(new WeightedScorePolicy()), (scores, path) -> {
                    exported.set(scores);
                    destination.set(path);
                });
        application.addStudent("Анна");
        application.addCriterion("Проект", 100, 1);
        application.evaluate(1, 1, 50);
        assertEquals(5, application.finalScores().get(0).value());

        application.correctEvaluation(1, 1, 80);
        application.exportFinalScores("results.csv");

        assertEquals(8, exported.get().get(0).value());
        assertEquals("results.csv", destination.get());
    }
}
