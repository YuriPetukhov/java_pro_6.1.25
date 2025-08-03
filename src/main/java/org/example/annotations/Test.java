package org.example.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Основная аннотация для пометки тестовых методов.
 *
 * <p>Методы, помеченные этой аннотацией, будут выполнены системой тестирования.
 * Можно указать приоритет выполнения, чтобы управлять порядком запуска тестов.</p>
 *
 * <p>Поддерживаемые значения приоритета: от {@code MIN_PRIORITY} (1) до {@code MAX_PRIORITY} (10).</p>
 *
 * <p>По умолчанию приоритет равен {@code 5}. Более высокий приоритет означает более раннее выполнение.</p>
 *
 * <p>Пример:</p>
 * <pre>{@code
 * @Test(priority = 9)
 * public void importantTest() { ... }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {

    int MIN_PRIORITY = 1;
    int MAX_PRIORITY = 10;

    /**
     * Приоритет выполнения теста (от 1 до 10).
     * @return целое значение приоритета
     */
    int priority() default 5;
}
