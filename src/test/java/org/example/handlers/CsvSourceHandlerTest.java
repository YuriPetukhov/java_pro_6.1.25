package org.example.handlers;

import org.example.annotations.CsvSource;
import org.example.core.PlanStep;
import org.example.core.TestContext;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CsvSourceHandlerTest {

    static class Dummy {

        Object[] argsReceived;

        @CsvSource("1, true, hello")
        public void parametrized(int a, boolean b, String c) {
            argsReceived = new Object[]{a, b, c};
        }
    }

    @Test
    void shouldParseAndStoreArgumentsViaArgumentProvider() throws Exception {
        Method method = Dummy.class.getDeclaredMethod("parametrized", int.class, boolean.class, String.class);
        TestContext context = new TestContext();
        CsvSourceHandler handler = new CsvSourceHandler();

        handler.handle(method, context);

        // Проверка, что метод зарегистрирован
        List<Method> registered = context.getMethodsFor(CsvSource.class);
        assertTrue(registered.contains(method));

        // Проверка через ArgumentProvider
        Object[] args = handler.getArgumentsFor(method, context);
        assertArrayEquals(new Object[]{1, true, "hello"}, args);
    }


    @Test
    void shouldBuildStepAndInvokeMethodWithArguments() throws Exception {
        Method method = Dummy.class.getDeclaredMethod("parametrized", int.class, boolean.class, String.class);
        TestContext context = new TestContext();
        CsvSourceHandler handler = new CsvSourceHandler();

        handler.handle(method, context);

        Dummy dummy = new Dummy();
        List<PlanStep> steps = handler.buildSteps(context, Dummy.class);

        for (PlanStep step : steps) {
            step.execute(dummy);
        }

        assertArrayEquals(new Object[]{1, true, "hello"}, dummy.argsReceived);
    }

    @Test
    void shouldThrowExceptionIfArgumentCountMismatch() {
        class Broken {
            @CsvSource("1, 2")
            public void method(int a) {}
        }

        CsvSourceHandler handler = new CsvSourceHandler();
        Method method = null;
        try {
            method = Broken.class.getDeclaredMethod("method", int.class);
        } catch (NoSuchMethodException e) {
            fail("Test setup failure");
        }

        TestContext context = new TestContext();

        Method finalMethod = method;
        assertThrows(IllegalArgumentException.class, () -> {
            handler.handle(finalMethod, context);
        });
    }
}
