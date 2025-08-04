package org.example.runner;

import org.example.core.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Построитель плана выполнения тестов.
 * <p>
 * Выполняет:
 * <ol>
 *     <li>Автоматический вызов всех {@link AnnotationHandler} на методах тестового класса</li>
 *     <li>Сбор {@link PlanStep} шагов от {@link PlanStepContributor}-ов</li>
 *     <li>Передачу {@link ArgumentProvider}-ов в {@link TestContext}</li>
 * </ol>
 */
public class ExecutionPlanner {

    /**
     * Формирует {@link ExecutionPlan} для заданного тестового класса.
     *
     * @param testClass класс с аннотированными тестовыми методами
     * @return готовый план исполнения
     */
    public static ExecutionPlan plan(Class<?> testClass) {
        TestContext context = new TestContext();

        // 1. Регистрируем всех аннотационных хендлеров
        HandlerRegistry registry = new HandlerRegistry();

        // 1.1 Находим всех ArgumentProvider-ов (например, CsvSourceHandler) и сохраняем в контекст
        List<ArgumentProvider> providers = registry.getHandlers().values().stream()
                .filter(h -> h instanceof ArgumentProvider)
                .map(h -> (ArgumentProvider) h)
                .toList();
        context.put("ARGUMENT_PROVIDERS", providers);

        // 2. Обрабатываем все методы тестового класса с помощью соответствующих AnnotationHandler-ов
        for (Method method : testClass.getDeclaredMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                AnnotationHandler handler = registry.get(annotation.annotationType());
                if (handler != null) {
                    handler.handle(method, context);
                } else {
                    System.out.printf("Warning: No handler found for annotation: %s%n", annotation.annotationType().getName());
                }
            }
        }

        // 3. Сбор всех шагов выполнения от PlanStepContributor-ов (Before/After/Test и др.)
        List<PlanStep> steps = new ArrayList<>();
        for (AnnotationHandler handler : registry.getHandlers().values()) {
            if (handler instanceof PlanStepContributor contributor) {
                steps.addAll(contributor.buildSteps(context, testClass));
            }
        }

        // 4. Возвращаем итоговый план исполнения
        return new ExecutionPlan(steps);
    }
}
