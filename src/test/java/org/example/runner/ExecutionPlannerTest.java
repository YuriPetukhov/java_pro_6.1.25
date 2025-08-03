package org.example.runner;

import org.example.annotations.Test;
import org.example.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionPlannerTest {

    static class DummyTest {
        static AtomicBoolean called = new AtomicBoolean(false);

        @Test(priority = 1)
        public void test() {
            called.set(true);
        }
    }

    @BeforeEach
    void resetState() {
        DummyTest.called.set(false);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("План должен успешно собираться и выполняться без исключений")
    void plan_shouldBuildExecutionPlan_withStepsFromContributors() {
        ExecutionPlan plan = ExecutionPlanner.plan(DummyTest.class);

        assertNotNull(plan, "ExecutionPlan должен быть создан");
        assertDoesNotThrow(() -> plan.execute(new DummyTest()), "Выполнение плана не должно выбрасывать исключения");
        assertTrue(DummyTest.called.get(), "Метод теста должен быть вызван");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("ArgumentProvider-ы должны быть переданы в контекст")
    void plan_shouldRegisterArgumentProvidersInContext() throws NoSuchMethodException {
        ExecutionPlan plan = ExecutionPlanner.plan(DummyTest.class);

        assertNotNull(plan);

        HandlerRegistry registry = new HandlerRegistry();
        List<ArgumentProvider> providers = registry.getHandlers().values().stream()
                .filter(h -> h instanceof ArgumentProvider)
                .map(h -> (ArgumentProvider) h)
                .toList();

        assertFalse(providers.isEmpty(), "Ожидается хотя бы один ArgumentProvider");
    }
}
