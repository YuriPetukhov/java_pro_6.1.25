package org.example.core;

import org.example.handlers.HandlerMarker;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Регистр всех аннотационных обработчиков ({@link AnnotationHandler}).
 *
 * <p>Автоматически находит и инстанцирует все классы, реализующие {@code AnnotationHandler},
 * с помощью библиотеки Reflections, ограничиваясь пакетом {@code org.example.handlers}.
 *
 * <p>Каждый обработчик регистрируется по типу аннотации, которую он обрабатывает
 * (определяется через {@link AnnotationHandler#annotationType()}).
 *
 * <p>Если обработчик также реализует интерфейс {@link PlanStepContributor}, он может быть
 * использован планировщиком для формирования шагов выполнения тестов.
 *
 * @see AnnotationHandler
 * @see PlanStepContributor
 */

public class HandlerRegistry {

    private final Map<Class<? extends Annotation>, AnnotationHandler> handlers = new HashMap<>();

    public HandlerRegistry() {
        Reflections reflections = new Reflections(HandlerMarker.class.getPackageName());
        Set<Class<? extends AnnotationHandler>> impls = reflections.getSubTypesOf(AnnotationHandler.class);

        for (Class<? extends AnnotationHandler> clazz : impls) {
            if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                try {
                    AnnotationHandler handler = clazz.getDeclaredConstructor().newInstance();
                    handlers.put(handler.annotationType(), handler);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to instantiate handler: " + clazz, e);
                }
            }
        }
    }

    public AnnotationHandler get(Class<? extends Annotation> annotationClass) {
        return handlers.get(annotationClass);
    }

    public Map<Class<? extends Annotation>, AnnotationHandler> getHandlers() {
        return Collections.unmodifiableMap(handlers);
    }

    /**
     * Возвращает список всех обработчиков, реализующих {@link PlanStepContributor}.
     *
     * <p>Эти обработчики участвуют в формировании плана выполнения тестов.
     *
     * @return список обработчиков, поддерживающих построение шагов
     */
    public List<PlanStepContributor> getStepContributors() {
        return handlers.values().stream()
                .filter(handler -> handler instanceof PlanStepContributor)
                .map(handler -> (PlanStepContributor) handler)
                .toList();
    }

}

