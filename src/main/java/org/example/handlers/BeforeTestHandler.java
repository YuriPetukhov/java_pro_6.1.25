package org.example.handlers;

import org.example.annotations.BeforeTest;
import org.example.core.TestContext;
import org.example.core.PlanStep;
import org.example.core.PlanStepContributor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик аннотации {@link BeforeTest}.
 * <p>
 * Регистрирует методы, которые должны выполняться перед каждым {@code @Test}-методом.
 * <p>
 * Метод должен быть нестатическим и без параметров.
 */
public class BeforeTestHandler implements PlanStepContributor {

    /**
     * Возвращает аннотацию, которую обрабатывает этот хендлер.
     */
    @Override
    public Class<? extends Annotation> annotationType() {
        return BeforeTest.class;
    }

    /**
     * Добавляет метод, помеченный {@code @BeforeTest}, в контекст выполнения.
     * Метод должен соответствовать следующим требованиям:
     * <ul>
     *     <li>не быть {@code static}</li>
     *     <li>не иметь параметров</li>
     * </ul>
     *
     * @param method  метод с аннотацией {@code @BeforeTest}
     * @param context контекст тестирования
     */
    @Override
    public void handle(Method method, TestContext context) {
        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException("@BeforeTest method must not be static: " + method.getName());
        }
        if (method.getParameterCount() > 0) {
            throw new IllegalStateException("@BeforeTest method must have no parameters: " + method.getName());
        }
        context.addAnnotatedMethod(BeforeTest.class, method);
    }

    /**
     * Создаёт шаги, которые будут выполняться перед каждым тестом.
     */
    @Override
    public List<PlanStep> buildSteps(TestContext context, Class<?> testClass) {
        List<PlanStep> steps = new ArrayList<>();
        for (Method method : context.getMethodsFor(BeforeTest.class)) {
            steps.add(method::invoke);
        }
        return steps;
    }
}
