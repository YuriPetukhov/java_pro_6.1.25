package org.example.core;

/**
 * Функциональный интерфейс, представляющий один шаг выполнения теста.
 *
 * Каждый шаг принимает инстанс тестового класса и выполняет определённое действие
 * (например, вызов метода с аннотацией @BeforeTest, @Test, @AfterTest и т.п.).
 *
 * Используется в ExecutionPlan для пошагового исполнения тестов.
 */
@FunctionalInterface
public interface PlanStep {
    void execute(Object instance) throws Exception;
}


