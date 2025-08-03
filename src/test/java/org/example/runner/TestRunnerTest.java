package org.example.runner;

import org.example.tests.BasicTestSuite;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class TestRunnerTest {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void redirectOutput() {
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    void runTests_shouldExecuteAllStepsInOrder() throws Exception {
        TestRunner.runTests(BasicTestSuite.class);
        String output = out.toString();

        assertTrue(output.contains("[BeforeSuite] Global test setup"));
        assertTrue(output.contains("[Test 10] High-priority logic executed"));
        assertTrue(output.contains("[Test 5] Default-priority logic executed"));
        assertTrue(output.contains("[Test 1] Low-priority logic executed"));
        assertTrue(output.contains("[AfterSuite] Global test cleanup"));
    }

    @Test
    void runTests_shouldThrowException_ifPlanFails() {
        RuntimeException outer = assertThrows(RuntimeException.class, () ->
                TestRunner.runTests(FailingTest.class)
        );

        assertEquals("Test execution failed", outer.getMessage());

        Throwable stepFailure = outer.getCause();
        assertNotNull(stepFailure);
        assertEquals("Step execution failed", stepFailure.getMessage());

        Throwable invocationEx = stepFailure.getCause();
        assertNotNull(invocationEx);
        assertInstanceOf(InvocationTargetException.class, invocationEx);

        Throwable boom = invocationEx.getCause();
        assertNotNull(boom);
        assertEquals("Boom", boom.getMessage());
    }



    @Test
    void runTests_shouldThrowException_ifConstructorFails() {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                TestRunner.runTests(NoDefaultConstructor.class)
        );
        assertTrue(ex.getCause() instanceof NoSuchMethodException);
    }

    static class FailingTest {
        @org.example.annotations.Test(priority = 5)
        public void test() {
            throw new RuntimeException("Boom");
        }
    }


    static class NoDefaultConstructor {
        public NoDefaultConstructor(String value) {}
    }



}
