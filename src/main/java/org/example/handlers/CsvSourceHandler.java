package org.example.handlers;

import org.example.annotations.CsvSource;
import org.example.core.ArgumentProvider;
import org.example.core.PlanStep;
import org.example.core.PlanStepContributor;
import org.example.core.TestContext;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Обработчик аннотации {@link CsvSource}.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Разбор CSV-аргументов из аннотации</li>
 *     <li>Сопоставление с параметрами метода</li>
 *     <li>Сохранение аргументов вызова в {@link TestContext}</li>
 * </ul>
 */
public class CsvSourceHandler implements PlanStepContributor, ArgumentProvider {

    // Локальное хранилище аргументов для каждого метода
    private final Map<Method, Object[]> methodArgs = new HashMap<>();

    @Override
    public Class<CsvSource> annotationType() {
        return CsvSource.class;
    }

    /**
     * Обрабатывает метод с аннотацией {@code @CsvSource}.
     * <ul>
     *     <li>Проверяет, что метод не является static</li>
     *     <li>Разбирает значения аргументов из CSV</li>
     *     <li>Сохраняет их для последующего использования</li>
     * </ul>
     *
     * @throws IllegalArgumentException если количество аргументов не совпадает с параметрами метода
     */
    @Override
    public void handle(Method method, TestContext context) {
        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException("@CsvSource method must not be static: " + method.getName());
        }

        CsvSource annotation = method.getAnnotation(CsvSource.class);
        String[] tokens = annotation.value().split("\\s*,\\s*");

        Class<?>[] paramTypes = method.getParameterTypes();
        if (tokens.length != paramTypes.length) {
            throw new IllegalArgumentException(
                    "CsvSource values count (" + tokens.length +
                            ") doesn't match method parameters count (" + paramTypes.length +
                            ") in: " + method.getName()
            );
        }

        Object[] parsed = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            parsed[i] = parse(tokens[i], paramTypes[i]);
        }

        // Сохраняем аргументы локально и в контексте
        methodArgs.put(method, parsed);
        context.addAnnotatedMethod(CsvSource.class, method);
    }

    /**
     * Строит шаги выполнения для всех методов с {@code @CsvSource},
     * включая передачу параметров, полученных из CSV.
     */
    @Override
    public List<PlanStep> buildSteps(TestContext context, Class<?> testClass) {
        List<PlanStep> steps = new ArrayList<>();

        for (Method method : context.getMethodsFor(CsvSource.class)) {
            Object[] args = methodArgs.getOrDefault(method, new Object[0]);
            steps.add(instance -> {
                method.setAccessible(true);
                method.invoke(instance, args);
            });
        }

        return steps;
    }

    /**
     * Возвращает массив аргументов, соответствующих указанному методу.
     * <p>
     * Используется {@link org.example.handlers.TestHandler} для вызова
     * параметризованных методов. Аргументы заранее парсятся и сохраняются
     * во время обработки аннотации {@code @CsvSource}.
     *
     * @param method  метод, для которого запрашиваются аргументы
     * @param context текущий контекст выполнения тестов (не используется в данной реализации)
     * @return массив аргументов, или пустой массив, если аргументы для метода не были заданы
     */
    @Override
    public Object[] getArgumentsFor(Method method, TestContext context) {
        return methodArgs.getOrDefault(method, new Object[0]);
    }


    /**
     * Преобразует строковое значение из CSV в нужный тип.
     *
     * @throws UnsupportedOperationException если тип параметра не поддерживается
     */
    private Object parse(String token, Class<?> type) {
        if (type == int.class || type == Integer.class) return Integer.parseInt(token);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(token);
        if (type == String.class) return token;
        if (type == double.class || type == Double.class) return Double.parseDouble(token);
        if (type == long.class || type == Long.class) return Long.parseLong(token);
        throw new UnsupportedOperationException("Unsupported parameter type: " + type);
    }
}
