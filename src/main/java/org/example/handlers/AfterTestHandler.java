package org.example.handlers;

import org.example.annotations.AfterTest;
import org.example.core.TestContext;
import org.example.core.PlanStep;
import org.example.core.PlanStepContributor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик аннотации {@link AfterTest}.
 *
 * <p>Регистрирует все методы, помеченные {@code @AfterTest}, и создаёт шаги,
 * которые будут выполняться после каждого тестового метода.</p>
 *
 * <p>Каждый такой метод должен быть нестатическим и без параметров.</p>
 */

public class AfterTestHandler implements PlanStepContributor {

    @Override
    public Class<AfterTest> annotationType() {
        return AfterTest.class;
    }

    /**
     * Регистрирует метод {@code @AfterTest} в контексте выполнения.
     *
     * @param method метод, помеченный аннотацией
     * @param context контекст, в котором сохраняются аннотированные методы
     */

    @Override
    public void handle(Method method, TestContext context) {
        // Сохраняем метод в контексте
        List<Method> list = context.getOrDefault(AfterTest.class, new ArrayList<>());
        list.add(method);
        context.put(AfterTest.class, list);
    }

    /**
     * Создаёт шаги выполнения для всех методов {@code @AfterTest}, зарегистрированных в контексте.
     *
     * @param context контекст с зарегистрированными методами
     * @param testClass тестовый класс (не используется)
     * @return список шагов для выполнения после каждого теста
     */

    @Override
    public List<PlanStep> buildSteps(TestContext context, Class<?> testClass) {
        List<PlanStep> steps = new ArrayList<>();

        List<Method> methods = context.getOrDefault(AfterTest.class, List.of());
        for (Method method : methods) {
            steps.add((Object instance) -> method.invoke(instance, new Object[0]));
        }

        return steps;
    }

}
