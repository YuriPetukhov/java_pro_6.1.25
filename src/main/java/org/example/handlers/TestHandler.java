package org.example.handlers;

import org.example.annotations.Test;
import org.example.core.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Обработчик аннотации {@link Test}.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Валидацию и регистрацию методов с {@code @Test} в {@link TestContext}</li>
 *     <li>Учет приоритетов выполнения</li>
 *     <li>Формирование шагов {@link PlanStep} для выполнения</li>
 *     <li>Получение аргументов с использованием {@link ArgumentProvider}</li>
 * </ul>
 */
public class TestHandler implements PlanStepContributor {

    @Override
    public Class<Test> annotationType() {
        return Test.class;
    }

    /**
     * Обрабатывает метод с аннотацией {@code @Test}.
     * Проверяет допустимость приоритета и сохраняет метод в контекст.
     *
     * @throws IllegalArgumentException если приоритет выходит за пределы {@link Test#MIN_PRIORITY} и {@link Test#MAX_PRIORITY}
     */
    @Override
    public void handle(Method method, TestContext context) {
        int priority = method.getAnnotation(Test.class).priority();

        if (priority < Test.MIN_PRIORITY || priority > Test.MAX_PRIORITY) {
            throw new IllegalArgumentException(
                    "@Test priority must be between " + Test.MIN_PRIORITY +
                            " and " + Test.MAX_PRIORITY + ": " + method.getName()
            );
        }

        context.addAnnotatedMethod(Test.class, method);
    }

    /**
     * Создаёт шаги {@link PlanStep} для всех методов с аннотацией {@code @Test},
     * упорядоченных по приоритету (по убыванию).
     * Аргументы для вызова извлекаются через всех зарегистрированных {@link ArgumentProvider}.
     */
    @Override
    public List<PlanStep> buildSteps(TestContext context, Class<?> testClass) {
        List<PlanStep> steps = new ArrayList<>();
        List<Method> testMethods = context.getMethodsFor(Test.class);

        List<ArgumentProvider> providers = context.getOrDefault("ARGUMENT_PROVIDERS", List.of());

        // Сортировка по убыванию приоритета
        testMethods.sort(Comparator.comparingInt(
                m -> -m.getAnnotation(Test.class).priority()
        ));

        for (Method method : testMethods) {
            Object[] args = resolveArguments(method, context, providers);
            steps.add(testInstance -> {
                method.setAccessible(true);
                method.invoke(testInstance, args);
            });
        }

        return steps;
    }

    /**
     * Извлекает аргументы для метода, обращаясь к доступным {@link ArgumentProvider}.
     */
    private Object[] resolveArguments(Method method, TestContext context, List<ArgumentProvider> providers) {
        for (ArgumentProvider provider : providers) {
            Object[] args = provider.getArgumentsFor(method, context);
            if (args != null && args.length > 0) {
                return args;
            }
        }
        return new Object[0];
    }
}
