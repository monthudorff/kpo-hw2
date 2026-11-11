package ru.hse.pos.domain;

public final class Criterion {
    private final int id;
    private final String name;
    private final double maxScore;
    private final double weight;

    private Criterion(Builder builder) {
        if (builder.id <= 0) {
            throw new DomainException("ID критерия должен быть положительным.");
        }
        if (builder.name == null || builder.name.isBlank()) {
            throw new DomainException("Название критерия не должно быть пустым.");
        }
        if (builder.maxScore == null || !Double.isFinite(builder.maxScore) || builder.maxScore <= 0) {
            throw new DomainException("Максимум баллов должен быть конечным числом больше нуля.");
        }
        if (builder.weight == null || !Double.isFinite(builder.weight) || builder.weight < 0) {
            throw new DomainException("Вес должен быть конечным неотрицательным числом.");
        }
        id = builder.id;
        name = builder.name.strip();
        maxScore = builder.maxScore;
        weight = builder.weight;
    }

    public int id() { return id; }
    public String name() { return name; }
    public double maxScore() { return maxScore; }
    public double weight() { return weight; }

    public static Builder builder(int id) {
        return new Builder(id);
    }

    public static final class Builder {
        private final int id;
        private String name;
        private Double maxScore;
        private Double weight;

        private Builder(int id) {
            this.id = id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder maxScore(double maxScore) {
            this.maxScore = maxScore;
            return this;
        }

        public Builder weight(double weight) {
            this.weight = weight;
            return this;
        }

        public Criterion build() {
            return new Criterion(this);
        }
    }
}
