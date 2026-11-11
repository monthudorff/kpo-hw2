package ru.hse.pos.domain;

import java.util.List;
import java.util.Objects;

public final class FinalScoreService {
    private final FinalScorePolicy policy;

    public FinalScoreService(FinalScorePolicy policy) {
        this.policy = Objects.requireNonNull(policy);
    }

    public FinalScore calculate(Gradebook gradebook, int studentId) {
        Student student = gradebook.student(studentId);
        return new FinalScore(student, policy.calculate(gradebook, studentId));
    }

    public List<FinalScore> calculateAll(Gradebook gradebook) {
        return gradebook.students().stream().map(student -> calculate(gradebook, student.id())).toList();
    }
}
