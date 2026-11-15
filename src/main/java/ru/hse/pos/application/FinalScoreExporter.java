package ru.hse.pos.application;

import ru.hse.pos.domain.FinalScore;

import java.util.List;

public interface FinalScoreExporter {
    void export(List<FinalScore> scores, String destination);
}
