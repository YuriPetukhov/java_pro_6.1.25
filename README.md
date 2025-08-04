# java_pro_6.1.25

**Учебный Java-фреймворк для аннотированного тестирования**

Проект реализует собственную систему исполнения тестов с поддержкой аннотаций вроде `@Test`, `@BeforeSuite`, `@CsvSource` и др.

---

## Возможности

- Поддержка кастомных аннотаций:
  - `@Test(priority = N)`
  - `@BeforeSuite`, `@AfterSuite`
  - `@BeforeTest`, `@AfterTest`
  - `@CsvSource`
- Система хуков и планов выполнения
- Расширяемая архитектура с `AnnotationHandler`, `PlanStepContributor`, `ArgumentProvider`
- Автоматическая регистрация хендлеров через Reflections API
- Интеграция с Codecov для отображения покрытия тестами

---

## Пример использования

```java
@BeforeSuite
public static void globalSetup() {
    System.out.println("Setup before all tests");
}

@BeforeTest
public void setUp() {
    System.out.println("Before each test");
}

@Test(priority = 3)
@CsvSource("1, true, Hello")
public void testExample(int num, boolean flag, String msg) {
    System.out.printf("Values: %d %b %s%n", num, flag, msg);
}

@AfterTest
public void tearDown() {
    System.out.println("After each test");
}

@AfterSuite
public static void globalCleanup() {
    System.out.println("Cleanup after all tests");
}
```

---

## Сборка и запуск

```bash
mvn clean verify
```

Также можно вручную запустить:

```java
TestRunner.runTests(MyTestClass.class);
```

---

## Покрытие тестами

Проект использует:

- [JaCoCo](https://www.eclemma.org/jacoco/) для генерации отчётов
- [Codecov](https://about.codecov.io/) для отображения в PR и GitHub UI

[Посмотреть отчёт на Codecov](https://app.codecov.io/gh/YuriPetukhov/java_pro_6.1.25)

---

## 📂 Структура проекта

```
src/
├── main/java/org/example/
│   ├── annotations/        // Аннотации
│   ├── core/               // ExecutionPlan, TestContext, интерфейсы
│   ├── handlers/           // Обработчики аннотаций
│   └── runner/             // TestRunner, ExecutionPlanner
└── test/java/org/example/  // Unit-тесты
```

---

## Зависимости

- Java 21
- Maven
- JUnit 5
- Reflections

---

## Автор

[Юрий Петухов](https://github.com/YuriPetukhov)

---