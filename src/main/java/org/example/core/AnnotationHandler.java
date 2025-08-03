package org.example.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Интерфейс для обработки пользовательских аннотаций тестов.
 *
 * Каждый хендлер реализует этот интерфейс и отвечает за обработку методов,
 * помеченных определённой аннотацией (например, @BeforeSuite, @Test, @CsvSource и т.д.).
 *
 * Используется в связке с {@link HandlerRegistry}, который собирает все реализации
 * интерфейса и вызывает {@code handle} для каждого метода с соответствующей аннотацией.
 */
public interface AnnotationHandler {

    /**
     * Возвращает тип аннотации, которую обрабатывает данный хендлер.
     * Используется в {@link HandlerRegistry} для сопоставления аннотации и её обработчика.
     *
     * @return класс аннотации (например, {@code BeforeSuite.class})
     */
    Class<? extends Annotation> annotationType();

    /**
     * Обрабатывает метод, помеченный поддерживаемой аннотацией.
     * Обычно хендлер сохраняет информацию о методе в {@link TestContext} для последующего выполнения.
     *
     * @param method  метод, помеченный аннотацией
     * @param context контекст выполнения тестов, куда можно сохранять данные
     */
    void handle(Method method, TestContext context);
}
