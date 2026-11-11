package ru.hse.pos;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import ru.hse.pos.application.FinalScoreExporter;
import ru.hse.pos.application.GradebookApplication;
import ru.hse.pos.application.GradebookRepository;
import ru.hse.pos.cli.ConsoleMenu;
import ru.hse.pos.domain.FinalScorePolicy;
import ru.hse.pos.domain.FinalScoreService;
import ru.hse.pos.domain.WeightedScorePolicy;
import ru.hse.pos.infrastructure.CsvFinalScoreExporter;
import ru.hse.pos.infrastructure.InMemoryGradebookRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        Guice.createInjector(new PosModule()).getInstance(ConsoleMenu.class).run();
    }

    public static final class PosModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(GradebookRepository.class).to(InMemoryGradebookRepository.class).in(Singleton.class);
            bind(FinalScoreExporter.class).to(CsvFinalScoreExporter.class);
            bind(FinalScorePolicy.class).to(WeightedScorePolicy.class);
        }

        @Provides
        FinalScoreService finalScoreService(FinalScorePolicy policy) {
            return new FinalScoreService(policy);
        }

        @Provides
        GradebookApplication application(GradebookRepository repository, FinalScoreService service,
                                        FinalScoreExporter exporter) {
            return new GradebookApplication(repository, service, exporter);
        }

        @Provides
        ConsoleMenu consoleMenu(GradebookApplication application) {
            return new ConsoleMenu(application,
                    new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)),
                    new PrintWriter(System.out, true, StandardCharsets.UTF_8));
        }
    }
}
