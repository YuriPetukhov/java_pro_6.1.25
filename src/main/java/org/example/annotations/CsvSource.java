package org.example.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для задания аргументов тестового метода в виде строки CSV.
 *
 * <p>Используется совместно с {@link org.example.annotations.Test} для параметризированных тестов.
 * Значения должны быть разделены запятыми, без кавычек. Типы параметров определяются по сигнатуре метода.</p>
 *
 * <p>Поддерживаемые типы: {@code int}, {@code long}, {@code double}, {@code boolean}, {@code String}.</p>
 *
 * <p>Пример:</p>
 * <pre>{@code
 * @Test
 * @CsvSource("42, hello, 3.14, true")
 * public void myTest(int x, String s, double d, boolean b) { ... }
 * }</pre>
 *
 * <p>Обрабатывается {@link org.example.handlers.CsvSourceHandler}.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CsvSource {
    String value();
}
