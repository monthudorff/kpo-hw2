package ru.hse.pos.application;

import ru.hse.pos.domain.Criterion;
import ru.hse.pos.domain.Evaluation;
import ru.hse.pos.domain.FinalScore;
import ru.hse.pos.domain.FinalScoreService;
import ru.hse.pos.domain.Student;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class GradebookApplication {
    private final GradebookRepository repository;
    private final FinalScoreService finalScoreService;
    private final FinalScoreExporter exporter;

    public GradebookApplication(GradebookRepository repository, FinalScoreService finalScoreService,
                                FinalScoreExporter exporter) {
        this.repository = Objects.requireNonNull(repository);
        this.finalScoreService = Objects.requireNonNull(finalScoreService);
        this.exporter = Objects.requireNonNull(exporter);
    }

    public Student addStudent(String name) {
        return repository.get().addStudent(name);
    }

    public Criterion addCriterion(String name, double maxScore, double weight) {
        return repository.get().addCriterion(name, maxScore, weight);
    }

    public List<Student> students() { return repository.get().students(); }
    public List<Criterion> criteria() { return repository.get().criteria(); }
    public Student student(int studentId) { return repository.get().student(studentId); }

    public void evaluate(int studentId, int criterionId, double score) {
        repository.get().evaluate(studentId, criterionId, score);
    }

    public Optional<Evaluation> evaluation(int studentId, int criterionId) {
        return repository.get().evaluation(studentId, criterionId);
    }

    public void correctEvaluation(int studentId, int criterionId, double score) {
        repository.get().correctEvaluation(studentId, criterionId, score);
    }

    public void cancelEvaluation(int studentId, int criterionId) {
        repository.get().cancelEvaluation(studentId, criterionId);
    }

    public List<FinalScore> finalScores() {
        return finalScoreService.calculateAll(repository.get());
    }

    public void exportFinalScores(String destination) {
        exporter.export(finalScores(), destination);
    }
}
