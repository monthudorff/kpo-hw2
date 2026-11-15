package ru.hse.pos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.hse.pos.domain.DomainException;
import ru.hse.pos.domain.Gradebook;

import static org.junit.jupiter.api.Assertions.*;

class GradebookTest {
    private Gradebook gradebook;

    @BeforeEach
    void arrange() {
        gradebook = new Gradebook();
        gradebook.addStudent("Анна");
        gradebook.addCriterion("Задачи", 10, 1);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 10})
    void acceptsScoreBoundaries(double score) {
        gradebook.evaluate(1, 1, score);

        assertEquals(score, gradebook.evaluation(1, 1).orElseThrow().score());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, 10.01, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void rejectsInvalidScoreWithoutAddingEvaluation(double score) {
        assertThrows(DomainException.class, () -> gradebook.evaluate(1, 1, score));
        assertTrue(gradebook.evaluation(1, 1).isEmpty());
    }

    @Test
    void rejectsCriterionOutsideRubric() {
        DomainException error = assertThrows(DomainException.class, () -> gradebook.evaluate(1, 99, 5));

        assertTrue(error.getMessage().contains("отсутствует в рубрикаторе"));
        assertTrue(gradebook.evaluation(1, 1).isEmpty());
    }

    @Test
    void rejectsUnknownStudent() {
        assertThrows(DomainException.class, () -> gradebook.evaluate(99, 1, 5));
    }

    @Test
    void failedCorrectionPreservesPreviousScore() {
        gradebook.evaluate(1, 1, 6);

        assertThrows(DomainException.class, () -> gradebook.correctEvaluation(1, 1, 11));

        assertEquals(6, gradebook.evaluation(1, 1).orElseThrow().score());
    }

    @Test
    void duplicateEvaluationRequiresExplicitCorrection() {
        gradebook.evaluate(1, 1, 6);

        assertThrows(DomainException.class, () -> gradebook.evaluate(1, 1, 7));

        assertEquals(6, gradebook.evaluation(1, 1).orElseThrow().score());
    }

    @Test
    void missingEvaluationCannotBeCorrectedOrCancelled() {
        assertThrows(DomainException.class, () -> gradebook.correctEvaluation(1, 1, 5));
        assertThrows(DomainException.class, () -> gradebook.cancelEvaluation(1, 1));
    }

    @Test
    void collectionsCannotBeUsedToBypassAggregate() {
        var students = gradebook.students();
        var criteria = gradebook.criteria();

        assertThrows(UnsupportedOperationException.class, students::clear);
        assertThrows(UnsupportedOperationException.class, criteria::clear);
        assertEquals(1, gradebook.students().size());
        assertEquals(1, gradebook.criteria().size());
    }

    @Test
    void blankStudentIsRejectedWithoutConsumingId() {
        assertThrows(DomainException.class, () -> gradebook.addStudent("  "));

        var student = gradebook.addStudent("Борис");

        assertEquals(2, student.id());
    }
}
