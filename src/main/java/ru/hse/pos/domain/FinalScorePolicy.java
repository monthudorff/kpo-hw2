package ru.hse.pos.domain;

public interface FinalScorePolicy {
    double calculate(Gradebook gradebook, int studentId);
}
