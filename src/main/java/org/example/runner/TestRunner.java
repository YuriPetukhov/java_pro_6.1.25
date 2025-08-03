package org.example.runner;

/**
 * Главный класс для запуска тестов.
 *
 * Делегирует построение плана исполнения {@link ExecutionPlanner},
 * а выполнение тестов — объекту {@link ExecutionPlan}.
 */
public class TestRunner {

    /**
     * Запускает тесты, определённые в указанном классе.
     *
     * @param testClass класс, содержащий аннотированные тестовые методы
     * @throws RuntimeException если создание экземпляра или выполнение тестов завершилось ошибкой
     */
    public static void runTests(Class<?> testClass) {
        try {
            // Создаём экземпляр тестового класса
            Object testInstance = testClass.getDeclaredConstructor().newInstance();

            // Строим план и запускаем шаги
            ExecutionPlan plan = ExecutionPlanner.plan(testClass);
            plan.execute(testInstance);

        } catch (Exception e) {
            System.err.println("Ошибка при выполнении тестов: " + e.getMessage());
            e.printStackTrace();

            // Оборачиваем в RuntimeException для корректной обработки в unit-тестах
            throw new RuntimeException("Test execution failed", e);
        }
    }
}
