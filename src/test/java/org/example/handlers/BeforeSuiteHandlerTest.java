package org.example.handlers;

import org.example.annotations.BeforeSuite;
import org.example.core.PlanStep;
import org.example.core.TestContext;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BeforeSuiteHandlerTest {

    static class DummySuite {
        static boolean prepared = false;

        @BeforeSuite
        public static void setup() {
            prepared = true;
        }

        public static void reset() {
            prepared = false;
        }
    }

    @Test
    void shouldRegisterBeforeSuiteMethod() throws Exception {
        Method method = DummySuite.class.getDeclaredMethod("setup");

        TestContext context = new TestContext();
        BeforeSuiteHandler handler = new BeforeSuiteHandler();

        handler.handle(method, context);

        List<Method> methods = context.getMethodsFor(BeforeSuite.class);
        assertEquals(1, methods.size());
        assertEquals(method, methods.get(0));
    }

    @Test
    void shouldInvokeBeforeSuiteStep() throws Exception {
        Method method = DummySuite.class.getDeclaredMethod("setup");

        TestContext context = new TestContext();
        BeforeSuiteHandler handler = new BeforeSuiteHandler();

        handler.handle(method, context);

        List<PlanStep> steps = handler.buildSteps(context, DummySuite.class);

        assertEquals(1, steps.size());

        DummySuite.reset();
        steps.get(0).execute(null); // static метод, поэтому передаём null

        assertTrue(DummySuite.prepared, "@BeforeSuite method was not invoked");
    }
}
