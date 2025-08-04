package org.example.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для метода, который должен выполняться перед каждым тестовым методом.
 *
 * <p>Можно пометить несколько методов в одном классе. Они будут вызываться перед каждым методом,
 * помеченным {@link org.example.annotations.Test}.</p>
 *
 * <p>Методы должны быть нестатическими и без аргументов.</p>
 *
 * <p>Обрабатывается с помощью {@link org.example.handlers.BeforeTestHandler}.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BeforeTest {}
