package org.example.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для методов, которые должны выполняться после каждого тестового метода.
 *
 * <p>Методы с этой аннотацией вызываются фреймворком после выполнения каждого метода,
 * помеченного {@link org.example.annotations.Test}.</p>
 *
 * <p>Могут быть нестатическими, и допускается несколько методов с этой аннотацией в одном классе.</p>
 *
 * <p>Обрабатывается с помощью {@link org.example.handlers.AfterTestHandler}.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterTest {}
