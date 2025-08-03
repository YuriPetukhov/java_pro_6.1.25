package org.example.handlers;

import org.example.annotations.BeforeSuite;
import org.example.core.TestContext;
import org.example.core.PlanStep;
import org.example.core.PlanStepContributor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик аннотации {@link BeforeSuite}.
 *
 * <p>Регистрирует методы, которые будут выполнены один раз перед запуском всех тестов.
 * Метод должен быть {@code static}, так как вызывается без экземпляра класса.</p>
 */
public class BeforeSuiteHandler implements PlanStepContributor {

    /**
     * Возвращает тип аннотации, которую обрабатывает данный хендлер.
     *
     * @return класс аннотации {@code BeforeSuite}
     */
    @Override
    public Class<? extends Annotation> annotationType() {
        return BeforeSuite.class;
    }

    /**
     * Обрабатывает метод, помеченный аннотацией {@code @BeforeSuite}, и сохраняет его в {@link TestContext}.
     *
     * <p>Методы сохраняются для последующего создания шагов выполнения.</p>
     *
     * @param method  метод с аннотацией
     * @param context текущий контекст тестирования
     */
    @Override
    public void handle(Method method, TestContext context) {
        if (!Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException("@BeforeSuite method must be static: " + method.getName());
        }
        context.addAnnotatedMethod(BeforeSuite.class, method);
    }


    /**
     * Создаёт шаги выполнения для всех методов {@code @BeforeSuite}, сохранённых в контексте.
     *
     * <p>Все шаги будут выполнены один раз перед началом всех тестов.</p>
     * <p>Предполагается, что методы {@code @BeforeSuite} являются {@code static}.</p>
     *
     * @param context   текущий контекст тестирования
     * @param testClass класс, содержащий тестовые методы (не используется)
     * @return список шагов выполнения
     */
    @Override
    public List<PlanStep> buildSteps(TestContext context, Class<?> testClass) {
        List<PlanStep> steps = new ArrayList<>();
        for (Method method : context.getMethodsFor(BeforeSuite.class)) {
            steps.add((instance) -> method.invoke(null)); // Метод должен быть static
        }
        return steps;
    }
}
