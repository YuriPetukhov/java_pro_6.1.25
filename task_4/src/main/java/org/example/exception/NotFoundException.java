package org.example.exception;

/**
 * Исключение для обозначения ситуации, когда запрашиваемый ресурс не найден.
 * <p>
 * Например, используется при поиске продукта или пользователя по идентификатору,
 * если такой сущности нет в базе данных.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Создаёт исключение с сообщением об ошибке.
     *
     * @param message текстовое описание, какое именно значение не найдено
     */
    public NotFoundException(String message) {
        super(message);
    }
}
