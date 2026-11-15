package ru.hse.pos.infrastructure;

import ru.hse.pos.application.GradebookRepository;
import ru.hse.pos.domain.Gradebook;

public final class InMemoryGradebookRepository implements GradebookRepository {
    private final Gradebook gradebook = new Gradebook();

    @Override
    public Gradebook get() {
        return gradebook;
    }
}
