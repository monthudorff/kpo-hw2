package ru.hse.pos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.hse.pos.domain.Criterion;
import ru.hse.pos.domain.DomainException;

import static org.junit.jupiter.api.Assertions.*;

class CriterionTest {
    @ParameterizedTest
    @ValueSource(doubles = {0, -1, Double.NaN, Double.POSITIVE_INFINITY})
    void rejectsInvalidMaximum(double maximum) {
        var builder = Criterion.builder(1).name("Проект").maxScore(maximum).weight(1);

        assertThrows(DomainException.class, builder::build);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, Double.NaN, Double.POSITIVE_INFINITY})
    void rejectsInvalidWeight(double weight) {
        var builder = Criterion.builder(1).name("Проект").maxScore(10).weight(weight);

        assertThrows(DomainException.class, builder::build);
    }

    @Test
    void incompleteBuilderCannotCreateCriterion() {
        assertAll(
                () -> assertThrows(DomainException.class,
                        () -> Criterion.builder(1).maxScore(10).weight(1).build()),
                () -> assertThrows(DomainException.class,
                        () -> Criterion.builder(1).name("Проект").weight(1).build()),
                () -> assertThrows(DomainException.class,
                        () -> Criterion.builder(1).name("Проект").maxScore(10).build()),
                () -> assertThrows(DomainException.class,
                        () -> Criterion.builder(1).name(" ").maxScore(10).weight(1).build()));
    }

    @Test
    void zeroWeightIsAllowed() {
        var criterion = Criterion.builder(1).name("Проект").maxScore(10).weight(0).build();

        assertEquals(0, criterion.weight());
    }
}
