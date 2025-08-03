package org.example.handlers;

import org.example.annotations.AfterTest;
import org.example.core.PlanStep;
import org.example.core.TestContext;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AfterTestHandlerTest {

    static class TestClass {
        static boolean called = false;

        @AfterTest
        public void cleanup() {
            called = true;
        }

        public static void reset() {
            called = false;
        }
    }

    @Test
    void shouldRegisterAfterTestMethod() throws Exception {
        Method method = TestClass.class.getDeclaredMethod("cleanup");

        TestContext context = new TestContext();
        AfterTestHandler handler = new AfterTestHandler();

        handler.handle(method, context);

        List<Method> methods = context.getOrDefault(AfterTest.class, List.of());
        assertEquals(1, methods.size());
        assertEquals(method, methods.get(0));
    }

    @Test
    void shouldInvokeAfterTestStep() throws Exception {
        Method method = TestClass.class.getDeclaredMethod("cleanup");

        TestContext context = new TestContext();
        AfterTestHandler handler = new AfterTestHandler();
        handler.handle(method, context);

        List<PlanStep> steps = handler.buildSteps(context, TestClass.class);

        assertEquals(1, steps.size());

        TestClass.reset();
        TestClass instance = new TestClass();

        steps.get(0).execute(instance);

        assertTrue(TestClass.called, "Method annotated with @AfterTest should have been invoked");
    }
}
