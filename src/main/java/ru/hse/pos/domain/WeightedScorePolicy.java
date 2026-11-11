package ru.hse.pos.domain;

public final class WeightedScorePolicy implements FinalScorePolicy {
    @Override
    public double calculate(Gradebook gradebook, int studentId) {
        gradebook.student(studentId);
        double maxWeight = gradebook.criteria().stream().mapToDouble(Criterion::weight).max().orElse(0);
        if (maxWeight == 0) {
            throw new DomainException("Нельзя рассчитать итог: сумма весов рубрикатора равна нулю.");
        }
        double weightedScore = 0;
        double totalWeight = 0;
        for (Criterion criterion : gradebook.criteria()) {
            // Масштабирование сохраняет формулу и не даёт сумме больших весов переполниться.
            double weight = criterion.weight() / maxWeight;
            double score = gradebook.evaluation(studentId, criterion.id()).map(Evaluation::score).orElse(0.0);
            weightedScore += weight * (score / criterion.maxScore());
            totalWeight += weight;
        }
        return 10 * (weightedScore / totalWeight);
    }
}
