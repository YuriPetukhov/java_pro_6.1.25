package org.example.handlers;

import org.example.annotations.BeforeTest;
import org.example.core.PlanStep;
import org.example.core.TestContext;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BeforeTestHandlerTest {

    static class Dummy {
        boolean called = false;

        @BeforeTest
        public void setup() {
            called = true;
        }
    }

    @Test
    void shouldRegisterBeforeTestMethod() throws Exception {
        Method method = Dummy.class.getDeclaredMethod("setup");
        TestContext context = new TestContext();
        BeforeTestHandler handler = new BeforeTestHandler();

        handler.handle(method, context);

        List<Method> methods = context.getMethodsFor(BeforeTest.class);
        assertEquals(1, methods.size());
        assertEquals(method, methods.get(0));
    }

    @Test
    void shouldInvokeBeforeTestStep() throws Exception {
        Method method = Dummy.class.getDeclaredMethod("setup");
        TestContext context = new TestContext();
        BeforeTestHandler handler = new BeforeTestHandler();

        handler.handle(method, context);
        List<PlanStep> steps = handler.buildSteps(context, Dummy.class);

        Dummy dummy = new Dummy();
        for (PlanStep step : steps) {
            step.execute(dummy);
        }

        assertTrue(dummy.called, "BeforeTest method was not invoked");
    }
}
