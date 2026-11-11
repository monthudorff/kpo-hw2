package ru.hse.pos;

import com.google.inject.Guice;
import org.junit.jupiter.api.Test;
import ru.hse.pos.application.GradebookApplication;
import ru.hse.pos.application.GradebookRepository;

import static org.junit.jupiter.api.Assertions.*;

class DependencyInjectionTest {
    @Test
    void guiceResolvesGraphAndSharesOneGradebookAcrossUseCases() {
        var injector = Guice.createInjector(new Main.PosModule());
        var first = injector.getInstance(GradebookApplication.class);
        var second = injector.getInstance(GradebookApplication.class);

        first.addStudent("Анна");

        assertEquals("Анна", second.students().get(0).name());
        assertSame(injector.getInstance(GradebookRepository.class),
                injector.getInstance(GradebookRepository.class));
    }
}
