package org.example.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для пометки метода, который должен быть выполнен один раз
 * после завершения всех тестов в тестовом классе.
 *
 * <p>Допустим только один метод с этой аннотацией в классе.
 * Метод должен быть {@code static} и не должен принимать аргументов.</p>
 *
 * <p>Обрабатывается фреймворком через {@link org.example.handlers.AfterSuiteHandler}.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterSuite {}
