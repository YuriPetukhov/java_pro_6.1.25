package org.example.handlers;

import org.example.annotations.AfterSuite;
import org.example.core.TestContext;
import org.example.core.PlanStep;
import org.example.core.PlanStepContributor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

/**
 * Обработчик аннотации {@link AfterSuite}.
 * <p>Регистрирует метод, помеченный {@code @AfterSuite}, который должен быть {@code static}
 * и вызывается один раз после завершения всех тестов.</p>
 */

public class AfterSuiteHandler implements PlanStepContributor {

    private Method afterSuiteMethod;

    @Override
    public Class<AfterSuite> annotationType() {
        return AfterSuite.class;
    }

    /**
     * Сохраняет метод с аннотацией {@code @AfterSuite}.
     * <p>Разрешён только один такой метод в классе, и он обязан быть {@code static}.</p>
     *
     * @param method метод, помеченный аннотацией
     * @param context контекст выполнения тестов
     * @throws IllegalStateException если метод не static или уже зарегистрирован другой
     */

    @Override
    public void handle(Method method, TestContext context) {
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException("@AfterSuite method must be static: " + method.getName());
        }
        if (afterSuiteMethod != null) {
            throw new IllegalStateException("Only one @AfterSuite method is allowed");
        }
        this.afterSuiteMethod = method;
    }

    /**
     * Возвращает шаг выполнения, соответствующий методу {@code @AfterSuite}, если он был найден.
     *
     * @param context контекст выполнения тестов
     * @param testClass класс, содержащий тесты
     * @return список шагов (может быть пустым, если метод не найден)
     */

    @Override
    public List<PlanStep> buildSteps(TestContext context, Class<?> testClass) {
        if (afterSuiteMethod == null) {
            return Collections.emptyList();
        }
        PlanStep step = (instance) -> afterSuiteMethod.invoke(null);
        return List.of(step);
    }

}
