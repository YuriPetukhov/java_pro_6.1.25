package org.example.runner;

import org.example.core.PlanStep;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionPlanTest {

    @Test
    void execute_shouldRunAllStepsInOrder() {
        List<String> trace = new ArrayList<>();
        PlanStep step1 = (instance) -> trace.add("step1");
        PlanStep step2 = (instance) -> trace.add("step2");

        ExecutionPlan plan = new ExecutionPlan(List.of(step1, step2));
        plan.execute(new Object());

        assertEquals(List.of("step1", "step2"), trace);
    }

    @Test
    void execute_shouldThrowRuntimeException_whenStepFails() {
        PlanStep failing = (instance) -> {
            throw new IllegalStateException("boom");
        };

        ExecutionPlan plan = new ExecutionPlan(List.of(failing));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> plan.execute(new Object()));
        assertEquals("Step execution failed", ex.getMessage());
        assertInstanceOf(IllegalStateException.class, ex.getCause());
        assertEquals("boom", ex.getCause().getMessage());
    }

    @Test
    void execute_shouldDoNothing_whenNoSteps() {
        ExecutionPlan plan = new ExecutionPlan(List.of());
        assertDoesNotThrow(() -> plan.execute(new Object()));
    }
}
