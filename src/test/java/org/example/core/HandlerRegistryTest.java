package org.example.core;

import org.example.annotations.AfterSuite;
import org.example.handlers.TestHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HandlerRegistryTest {

    private HandlerRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new HandlerRegistry();
    }

    @Test
    @DisplayName("Все обработчики аннотаций зарегистрированы")
    void test_allHandlersRegistered() {
        Map<Class<? extends Annotation>, AnnotationHandler> map = registry.getHandlers();
        assertFalse(map.isEmpty(), "Handler map should not be empty");

        assertTrue(map.containsKey(org.example.annotations.Test.class),
                "@Test annotation should be handled");

        AnnotationHandler testHandler = map.get(org.example.annotations.Test.class);
        assertNotNull(testHandler);
        assertEquals(TestHandler.class, testHandler.getClass());
    }

    @Test
    @DisplayName("Карта обработчиков недоступна для изменения извне")
    void test_handlerMapImmutable_fromOutside() {
        Map<Class<? extends Annotation>, AnnotationHandler> map = registry.getHandlers();
        assertThrows(UnsupportedOperationException.class, () -> {
            map.put(AfterSuite.class, null);
        }, "Handler map should not be modifiable from outside");
    }

    @Test
    @DisplayName("PlanStepContributor извлекаются корректно")
    void test_stepContributorsDetected() {
        List<PlanStepContributor> contributors = registry.getStepContributors();
        assertFalse(contributors.isEmpty(), "Step contributors list should not be empty");

        for (PlanStepContributor contributor : contributors) {
            assertNotNull(contributor.annotationType(), "Each contributor should have an annotation type");
        }
    }
}
