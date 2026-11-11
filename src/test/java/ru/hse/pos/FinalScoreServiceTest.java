package ru.hse.pos;

import org.junit.jupiter.api.Test;
import ru.hse.pos.domain.DomainException;
import ru.hse.pos.domain.FinalScoreService;
import ru.hse.pos.domain.Gradebook;
import ru.hse.pos.domain.WeightedScorePolicy;

import static org.junit.jupiter.api.Assertions.*;

class FinalScoreServiceTest {
    private final FinalScoreService service = new FinalScoreService(new WeightedScorePolicy());

    @Test
    void weightsNormalizedScoresWithDifferentMaximums() {
        Gradebook gradebook = gradebook();
        gradebook.evaluate(1, 1, 10);
        gradebook.evaluate(1, 2, 50);

        double result = service.calculate(gradebook, 1).value();

        assertEquals(6.25, result, 1e-10);
    }

    @Test
    void missingEvaluationContributesZeroButKeepsWeight() {
        Gradebook gradebook = gradebook();
        gradebook.evaluate(1, 1, 10);

        double result = service.calculate(gradebook, 1).value();

        assertEquals(2.5, result, 1e-10);
    }

    @Test
    void allMissingEvaluationsGiveZero() {
        Gradebook gradebook = gradebook();

        double result = service.calculate(gradebook, 1).value();

        assertEquals(0, result);
    }

    @Test
    void correctionAndCancellationRecalculateFromCurrentState() {
        Gradebook gradebook = gradebook();
        gradebook.evaluate(1, 1, 4);
        assertEquals(1, service.calculate(gradebook, 1).value(), 1e-10);

        gradebook.correctEvaluation(1, 1, 8);
        assertEquals(2, service.calculate(gradebook, 1).value(), 1e-10);
        gradebook.cancelEvaluation(1, 1);

        assertEquals(0, service.calculate(gradebook, 1).value());
        assertTrue(gradebook.evaluation(1, 1).isEmpty());
    }

    @Test
    void addingCriterionChangesDenominatorImmediately() {
        Gradebook gradebook = new Gradebook();
        gradebook.addStudent("Анна");
        gradebook.addCriterion("Задачи", 10, 1);
        gradebook.evaluate(1, 1, 10);

        gradebook.addCriterion("Проект", 100, 1);

        assertEquals(5, service.calculate(gradebook, 1).value(), 1e-10);
    }

    @Test
    void rejectsEmptyRubricAndZeroWeightSum() {
        Gradebook gradebook = new Gradebook();
        gradebook.addStudent("Анна");
        assertThrows(DomainException.class, () -> service.calculate(gradebook, 1));

        gradebook.addCriterion("Необязательное", 10, 0);

        DomainException error = assertThrows(DomainException.class, () -> service.calculate(gradebook, 1));
        assertTrue(error.getMessage().contains("сумма весов"));
    }

    @Test
    void largeFiniteWeightsDoNotOverflow() {
        Gradebook gradebook = new Gradebook();
        gradebook.addStudent("Анна");
        gradebook.addCriterion("Задачи", 10, Double.MAX_VALUE);
        gradebook.addCriterion("Проект", 100, Double.MAX_VALUE);
        gradebook.evaluate(1, 1, 10);

        double result = service.calculate(gradebook, 1).value();

        assertEquals(5, result, 1e-10);
    }

    @Test
    void domainServiceUsesSuppliedStrategy() {
        Gradebook gradebook = gradebook();
        FinalScoreService alternative = new FinalScoreService((book, id) -> 7);

        double result = alternative.calculate(gradebook, 1).value();

        assertEquals(7, result);
    }

    private Gradebook gradebook() {
        Gradebook gradebook = new Gradebook();
        gradebook.addStudent("Анна");
        gradebook.addCriterion("Задачи", 10, 1);
        gradebook.addCriterion("Проект", 100, 3);
        return gradebook;
    }
}
