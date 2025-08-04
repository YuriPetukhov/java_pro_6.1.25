package org.example.core;

import org.example.runner.ExecutionPlan;

import java.util.List;

/**
 * Интерфейс для аннотационных обработчиков, которые могут вносить шаги
 * в план выполнения тестов.
 *
 * <p>Реализуется теми {@link AnnotationHandler}, которые не только
 * обрабатывают аннотации, но и формируют шаги {@link PlanStep}, соответствующие
 * логике этих аннотаций (например, @BeforeTest, @Test, @AfterTest и т.п.).</p>
 *
 * <p>Позволяет расширять поведение тестового раннера без изменения его кода —
 * каждый новый хендлер сам добавляет необходимые шаги в {@link ExecutionPlan}.</p>
 */
public interface PlanStepContributor extends AnnotationHandler {

    /**
     * Построение шагов выполнения на основе данных в контексте и тестовом классе.
     *
     * @param context  текущий контекст тестирования
     * @param testClass класс, содержащий аннотированные методы
     * @return список шагов, которые должны быть добавлены в ExecutionPlan
     */
    List<PlanStep> buildSteps(TestContext context, Class<?> testClass);
}
