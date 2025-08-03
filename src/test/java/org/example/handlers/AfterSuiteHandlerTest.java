package org.example.handlers;

import org.example.annotations.AfterSuite;
import org.example.core.TestContext;
import org.example.core.PlanStep;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AfterSuiteHandlerTest {

    static class ValidTestClass {
        @AfterSuite
        public static void tearDown() {}
    }

    static class InvalidTestClass {
        @AfterSuite
        public void notStaticMethod() {}
    }

    @Test
    void shouldAcceptValidStaticMethod() throws Exception {
        Method method = ValidTestClass.class.getDeclaredMethod("tearDown");
        TestContext context = new TestContext();
        AfterSuiteHandler handler = new AfterSuiteHandler();

        handler.handle(method, context);

        List<PlanStep> steps = handler.buildSteps(context, ValidTestClass.class);
        assertEquals(1, steps.size());
    }

    @Test
    void shouldRejectNonStaticMethod() throws Exception {
        Method method = InvalidTestClass.class.getDeclaredMethod("notStaticMethod");
        TestContext context = new TestContext();
        AfterSuiteHandler handler = new AfterSuiteHandler();

        Exception ex = assertThrows(IllegalStateException.class, () -> handler.handle(method, context));
        assertTrue(ex.getMessage().contains("must be static"));
    }

    @Test
    void shouldRejectDuplicateAfterSuite() throws Exception {
        Method method = ValidTestClass.class.getDeclaredMethod("tearDown");
        TestContext context = new TestContext();
        AfterSuiteHandler handler = new AfterSuiteHandler();

        handler.handle(method, context); // первый раз — успех
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                handler.handle(method, context) // второй раз — ошибка
        );
        assertTrue(ex.getMessage().contains("Only one"));
    }
}
