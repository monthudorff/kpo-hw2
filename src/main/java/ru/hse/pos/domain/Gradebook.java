package ru.hse.pos.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Gradebook {
    private final Map<Integer, Student> students = new LinkedHashMap<>();
    private final Map<Integer, Criterion> criteria = new LinkedHashMap<>();
    private final Map<EvaluationKey, Evaluation> evaluations = new LinkedHashMap<>();
    private int nextStudentId = 1;
    private int nextCriterionId = 1;

    public Student addStudent(String name) {
        Student student = new Student(nextStudentId, name);
        students.put(student.id(), student);
        nextStudentId++;
        return student;
    }

    public Criterion addCriterion(String name, double maxScore, double weight) {
        Criterion criterion = Criterion.builder(nextCriterionId)
                .name(name).maxScore(maxScore).weight(weight).build();
        criteria.put(criterion.id(), criterion);
        nextCriterionId++;
        return criterion;
    }

    public void evaluate(int studentId, int criterionId, double score) {
        EvaluationKey key = checkedKey(studentId, criterionId);
        if (evaluations.containsKey(key)) {
            throw new DomainException("Оценка уже выставлена. Используйте исправление.");
        }
        evaluations.put(key, Evaluation.create(student(studentId), criterion(criterionId), score));
    }

    public void correctEvaluation(int studentId, int criterionId, double score) {
        EvaluationKey key = checkedKey(studentId, criterionId);
        requireEvaluation(key);
        evaluations.put(key, Evaluation.create(student(studentId), criterion(criterionId), score));
    }

    public void cancelEvaluation(int studentId, int criterionId) {
        EvaluationKey key = checkedKey(studentId, criterionId);
        requireEvaluation(key);
        evaluations.remove(key);
    }

    public Optional<Evaluation> evaluation(int studentId, int criterionId) {
        return Optional.ofNullable(evaluations.get(checkedKey(studentId, criterionId)));
    }

    public List<Student> students() { return List.copyOf(students.values()); }
    public List<Criterion> criteria() { return List.copyOf(criteria.values()); }

    public Student student(int id) {
        Student student = students.get(id);
        if (student == null) {
            throw new DomainException("Неизвестный студент: " + id + ".");
        }
        return student;
    }

    private Criterion criterion(int id) {
        Criterion criterion = criteria.get(id);
        if (criterion == null) {
            throw new DomainException("Критерий " + id + " отсутствует в рубрикаторе.");
        }
        return criterion;
    }

    private EvaluationKey checkedKey(int studentId, int criterionId) {
        student(studentId);
        criterion(criterionId);
        return new EvaluationKey(studentId, criterionId);
    }

    private void requireEvaluation(EvaluationKey key) {
        if (!evaluations.containsKey(key)) {
            throw new DomainException("Оценка ещё не выставлена.");
        }
    }

    private record EvaluationKey(int studentId, int criterionId) { }
}
