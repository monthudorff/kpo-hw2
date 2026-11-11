package ru.hse.pos.domain;

public record Student(int id, String name) {
    public Student {
        if (id <= 0) {
            throw new DomainException("ID студента должен быть положительным.");
        }
        if (name == null || name.isBlank()) {
            throw new DomainException("Имя студента не должно быть пустым.");
        }
        name = name.strip();
    }
}
