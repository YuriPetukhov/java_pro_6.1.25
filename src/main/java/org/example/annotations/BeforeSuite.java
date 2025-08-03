package org.example.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для метода, который должен выполняться один раз перед всеми тестами в тестовом классе.
 *
 * <p>Допускается только один метод с этой аннотацией на класс. Метод обязан быть {@code static}.</p>
 *
 * <p>Вызывается перед выполнением всех {@link org.example.annotations.Test}-методов.</p>
 *
 * <p>Обрабатывается с помощью {@link org.example.handlers.BeforeSuiteHandler}.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeSuite {}
