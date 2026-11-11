package ru.hse.pos.cli;

import ru.hse.pos.application.ExportException;
import ru.hse.pos.application.GradebookApplication;
import ru.hse.pos.domain.Criterion;
import ru.hse.pos.domain.DomainException;
import ru.hse.pos.domain.Evaluation;
import ru.hse.pos.domain.FinalScore;
import ru.hse.pos.domain.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class ConsoleMenu {
    private final GradebookApplication application;
    private final BufferedReader input;
    private final PrintWriter output;

    public ConsoleMenu(GradebookApplication application, BufferedReader input, PrintWriter output) {
        this.application = Objects.requireNonNull(application);
        this.input = Objects.requireNonNull(input);
        this.output = Objects.requireNonNull(output);
    }

    public void run() {
        output.println("ПОС — Преподавательская оценка студентов");
        output.println("Данные хранятся в памяти до выхода. Дробные числа: точка или запятая.");
        try {
            while (true) {
                printMenu();
                try {
                    switch (read("Выберите действие: ")) {
                        case "0" -> {
                            output.println("До свидания!");
                            return;
                        }
                        case "1" -> addStudent();
                        case "2" -> listStudents();
                        case "3" -> addCriterion();
                        case "4" -> listCriteria();
                        case "5" -> evaluate(false);
                        case "6" -> showEvaluations();
                        case "7" -> evaluate(true);
                        case "8" -> cancelEvaluation();
                        case "9" -> showFinalScores();
                        case "10" -> export();
                        default -> output.println("Ошибка: выберите пункт меню от 0 до 10.");
                    }
                } catch (DomainException | ExportException | IllegalArgumentException e) {
                    output.println("Ошибка: " + e.getMessage());
                }
            }
        } catch (EndOfInput e) {
            output.println("\nВвод завершён.");
        } catch (IOException e) {
            output.println("Ошибка чтения ввода: " + e.getMessage());
        } finally {
            output.flush();
        }
    }

    private void printMenu() {
        output.println("""

                1 — Добавить студента
                2 — Список студентов
                3 — Добавить критерий
                4 — Рубрикатор
                5 — Выставить оценку
                6 — Посмотреть оценки студента
                7 — Исправить оценку
                8 — Отменить оценку
                9 — Рассчитать итоги
                10 — Экспортировать итоги в CSV
                0 — Выход""");
    }

    private void addStudent() throws IOException {
        Student student = application.addStudent(read("Имя студента: "));
        output.println("Добавлен студент #" + student.id() + ": " + student.name());
    }

    private void listStudents() {
        List<Student> students = application.students();
        if (students.isEmpty()) {
            output.println("Студентов пока нет.");
        }
        for (Student student : students) {
            output.println("#" + student.id() + " — " + student.name());
        }
    }

    private void addCriterion() throws IOException {
        String name = read("Название критерия: ");
        double maxScore = readNumber("Максимум баллов: ");
        double weight = readNumber("Вес: ");
        Criterion criterion = application.addCriterion(name, maxScore, weight);
        output.println("Добавлен критерий #" + criterion.id() + ": " + criterion.name());
    }

    private void listCriteria() {
        List<Criterion> criteria = application.criteria();
        if (criteria.isEmpty()) {
            output.println("Рубрикатор пока пуст.");
        }
        for (Criterion criterion : criteria) {
            output.println("#" + criterion.id() + " — " + criterion.name()
                    + ", максимум: " + criterion.maxScore() + ", вес: " + criterion.weight());
        }
    }

    private void evaluate(boolean correction) throws IOException {
        int studentId = readId("ID студента: ");
        int criterionId = readId("ID критерия: ");
        double score = readNumber("Балл: ");
        if (correction) {
            application.correctEvaluation(studentId, criterionId, score);
            output.println("Оценка исправлена.");
        } else {
            application.evaluate(studentId, criterionId, score);
            output.println("Оценка выставлена.");
        }
    }

    private void showEvaluations() throws IOException {
        int studentId = readId("ID студента: ");
        output.println("Оценки: " + application.student(studentId).name());
        if (application.criteria().isEmpty()) {
            output.println("Рубрикатор пока пуст.");
        }
        for (Criterion criterion : application.criteria()) {
            String score = application.evaluation(studentId, criterion.id())
                    .map(Evaluation::score).map(Object::toString).orElse("не выставлена");
            output.println("#" + criterion.id() + " " + criterion.name() + ": " + score
                    + " / " + criterion.maxScore());
        }
    }

    private void cancelEvaluation() throws IOException {
        int studentId = readId("ID студента: ");
        int criterionId = readId("ID критерия: ");
        application.cancelEvaluation(studentId, criterionId);
        output.println("Оценка отменена.");
    }

    private void showFinalScores() {
        List<FinalScore> scores = application.finalScores();
        if (scores.isEmpty()) {
            output.println("Студентов пока нет.");
        }
        for (FinalScore score : scores) {
            output.printf(Locale.ROOT, "#%d %s — итог: %.2f / 10%n",
                    score.student().id(), score.student().name(), score.value());
        }
    }

    private void export() throws IOException {
        String destination = read("Путь CSV (существующий файл будет перезаписан): ");
        if (destination.isBlank()) {
            throw new IllegalArgumentException("Укажите путь к CSV-файлу.");
        }
        application.exportFinalScores(destination);
        output.println("Итоги экспортированы: " + destination);
    }

    private String read(String prompt) throws IOException {
        output.print(prompt);
        output.flush();
        String line = input.readLine();
        if (line == null) {
            throw new EndOfInput();
        }
        return line.strip();
    }

    private int readId(String prompt) throws IOException {
        try {
            return Integer.parseInt(read(prompt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID должен быть целым числом.");
        }
    }

    private double readNumber(String prompt) throws IOException {
        try {
            double value = Double.parseDouble(read(prompt).replace(',', '.'));
            if (!Double.isFinite(value)) {
                throw new NumberFormatException();
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Введите конечное число, например 7.5.");
        }
    }

    private static final class EndOfInput extends RuntimeException { }
}
