package org.example.runner;

import org.example.core.PlanStep;

import java.util.List;

/**
 * Представляет план выполнения тестов.
 * <p>
 * План включает все шаги, которые необходимо выполнить в рамках тестирования:
 * хуки (@BeforeSuite, @BeforeTest, @AfterTest, @AfterSuite), сами тесты, параметризованные вызовы и т.д.
 */
public class ExecutionPlan {
    private final List<PlanStep> steps;

    /**
     * Создаёт новый план выполнения на основе переданных шагов.
     *
     * @param steps список шагов (PlanStep), сформированных обработчиками аннотаций
     */
    public ExecutionPlan(List<PlanStep> steps) {
        this.steps = steps;
    }

    /**
     * Выполняет все шаги из плана последовательно.
     *
     * @param testInstance экземпляр тестового класса, передаваемый в методы (если требуется)
     * @throws RuntimeException если один из шагов завершился с ошибкой
     */
    public void execute(Object testInstance) {
        for (PlanStep step : steps) {
            try {
                step.execute(testInstance);
            } catch (Exception e) {
                throw new RuntimeException("Step execution failed", e);
            }
        }
    }
}
