package ru.hse.pos.domain;

public final class Evaluation {
    private final int studentId;
    private final int criterionId;
    private final double score;

    private Evaluation(Student student, Criterion criterion, double score) {
        if (!Double.isFinite(score) || score < 0 || score > criterion.maxScore()) {
            throw new DomainException("Балл должен быть в диапазоне [0, " + criterion.maxScore() + "].");
        }
        this.studentId = student.id();
        this.criterionId = criterion.id();
        this.score = score;
    }

    static Evaluation create(Student student, Criterion criterion, double score) {
        return new Evaluation(student, criterion, score);
    }

    public int studentId() { return studentId; }
    public int criterionId() { return criterionId; }
    public double score() { return score; }
}
