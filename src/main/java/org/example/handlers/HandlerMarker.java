package org.example.handlers;

/**
 * Маркерный класс, используемый для указания пакета, в котором следует искать обработчики аннотаций.
 *
 * Этот класс не содержит логики и служит ориентиром для библиотеки Reflections,
 * чтобы определить нужный пакет:
 *
 * <pre>
 * Reflections reflections = new Reflections(HandlerMarker.class.getPackageName());
 * </pre>
 *
 * Такой подход делает код менее чувствительным к переименованию или перемещению пакетов.
 */
public final class HandlerMarker {}
