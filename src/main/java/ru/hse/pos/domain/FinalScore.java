package ru.hse.pos.domain;

public record FinalScore(Student student, double value) {
    public FinalScore {
        if (student == null || !Double.isFinite(value) || value < 0 || value > 10) {
            throw new DomainException("Итог должен принадлежать студенту и находиться в диапазоне [0, 10].");
        }
    }
}
