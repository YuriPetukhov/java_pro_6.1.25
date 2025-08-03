package org.example.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Контекст выполнения тестов, предоставляющий общее хранилище данных
 * для аннотационных обработчиков и построителей шагов.
 *
 * <p>Позволяет:</p>
 * <ul>
 *     <li>регистрировать и извлекать методы, помеченные определёнными аннотациями</li>
 *     <li>хранить произвольные данные, доступные всем обработчикам в рамках одного прогона тестов</li>
 * </ul>
 *
 * <p>Ключами в хранилище могут быть классы аннотаций, строки или другие уникальные идентификаторы.</p>
 */
public class TestContext {

    // Универсальное хранилище данных
    private final Map<Object, Object> data = new HashMap<>();

    /**
     * Помещает значение в контекст по указанному ключу.
     *
     * @param key ключ, по которому сохраняется значение
     * @param value сохраняемое значение
     * @param <T> тип сохраняемого значения
     */
    public <T> void put(Object key, T value) {
        data.put(key, value);
    }

    /**
     * Извлекает значение по ключу.
     *
     * @param key ключ, по которому производится поиск
     * @param <T> ожидаемый тип результата
     * @return значение, либо {@code null}, если ключ не найден
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Object key) {
        return (T) data.get(key);
    }

    /**
     * Извлекает значение по ключу или возвращает значение по умолчанию.
     *
     * @param key ключ поиска
     * @param defaultValue значение по умолчанию
     * @param <T> ожидаемый тип результата
     * @return значение из контекста или defaultValue, если ключ не найден
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(Object key, T defaultValue) {
        return (T) data.getOrDefault(key, defaultValue);
    }

    /**
     * Добавляет метод, помеченный заданной аннотацией.
     * Используется обработчиками аннотаций для регистрации методов.
     *
     * @param annotationType класс аннотации
     * @param method метод, помеченный этой аннотацией
     */
    public void addAnnotatedMethod(Class<? extends Annotation> annotationType, Method method) {
        List<Method> methods = getOrDefault(annotationType, new ArrayList<>());
        methods.add(method);
        put(annotationType, methods);
    }

    /**
     * Возвращает список всех методов, зарегистрированных по заданной аннотации.
     *
     * @param annotationType класс аннотации
     * @return список методов, помеченных этой аннотацией
     */
    public List<Method> getMethodsFor(Class<? extends Annotation> annotationType) {
        return getOrDefault(annotationType, List.of());
    }
}
