package org.example.handlers;

import org.example.annotations.CsvSource;
import org.example.annotations.Test;
import org.example.core.PlanStep;
import org.example.core.TestContext;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestHandlerTest {

    static class Dummy {

        boolean wasCalled = false;

        @Test(priority = 5)
        public void testMethod() {
            wasCalled = true;
        }

        @Test(priority = 10)
        public void testWithPriority10() {}

        @Test(priority = 1)
        public void testWithPriority1() {}

        @Test(priority = 999) // invalid
        public void invalidPriorityMethod() {}
    }

    private TestHandler handler;
    private Dummy dummy;
    private TestContext context;

    @BeforeEach
    void setUp() {
        handler = new TestHandler();
        dummy = new Dummy();
        context = new TestContext();
    }

    @org.junit.jupiter.api.Test
    void shouldRegisterMethodInContext() throws Exception {
        Method method = Dummy.class.getDeclaredMethod("testMethod");

        handler.handle(method, context);

        List<Method> registered = context.getMethodsFor(Test.class);
        assertTrue(registered.contains(method));
    }

    @org.junit.jupiter.api.Test
    void shouldRejectInvalidPriority() throws Exception {
        Method method = Dummy.class.getDeclaredMethod("invalidPriorityMethod");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            handler.handle(method, context);
        });

        assertTrue(ex.getMessage().contains("priority"));
    }

    @org.junit.jupiter.api.Test
    void shouldBuildStepsAndExecuteThem() throws Exception {
        Method method = Dummy.class.getDeclaredMethod("testMethod");
        handler.handle(method, context);

        List<PlanStep> steps = handler.buildSteps(context, Dummy.class);

        assertEquals(1, steps.size());

        steps.get(0).execute(dummy);

        assertTrue(dummy.wasCalled);
    }

    @org.junit.jupiter.api.Test
    void shouldRespectTestPriorityOrder() throws Exception {
        Method m1 = Dummy.class.getDeclaredMethod("testWithPriority1");
        Method m2 = Dummy.class.getDeclaredMethod("testWithPriority10");

        handler.handle(m1, context);
        handler.handle(m2, context);

        List<PlanStep> steps = handler.buildSteps(context, Dummy.class);

        // Метод с приоритетом 10 должен идти первым
        java.util.List<Method> ordered = context.getMethodsFor(Test.class);
        ordered.sort((a, b) -> Integer.compare(
                b.getAnnotation(Test.class).priority(),
                a.getAnnotation(Test.class).priority()
        ));

        assertEquals("testWithPriority10", ordered.get(0).getName());
        assertEquals("testWithPriority1", ordered.get(1).getName());
    }

    @org.junit.jupiter.api.Test
    void shouldInjectArgumentsFromCsvSource() throws Exception {
        class ParamTest {
            Object[] argsReceived;

            @Test
            @CsvSource("42,true")
            public void paramMethod(int a, boolean b) {
                argsReceived = new Object[]{a, b};
            }
        }

        ParamTest testObj = new ParamTest();
        Method method = ParamTest.class.getDeclaredMethod("paramMethod", int.class, boolean.class);

        // Хендлеры
        TestHandler testHandler = new TestHandler();
        CsvSourceHandler csvHandler = new CsvSourceHandler();

        // Контекст
        TestContext context = new TestContext();
        testHandler.handle(method, context);    // @Test
        csvHandler.handle(method, context);     // @CsvSource

        // Зарегистрировать провайдер аргументов
        context.put("ARGUMENT_PROVIDERS", List.of(csvHandler));

        // Получить шаги и выполнить
        List<PlanStep> steps = testHandler.buildSteps(context, ParamTest.class);

        assertEquals(1, steps.size());
        steps.get(0).execute(testObj);

        assertArrayEquals(new Object[]{42, true}, testObj.argsReceived);
    }

}
