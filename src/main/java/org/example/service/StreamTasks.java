package org.example.service;

import org.example.model.Employee;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamTasks {

//    Найдите в списке целых чисел 3-е наибольшее число
//    (пример: 5 2 10 9 4 3 10 1 13 => 10)
    public static int thirdMax(List<Integer> numbers) {
        return numbers.stream()
                .sorted(Comparator.reverseOrder())
                .skip(2)
                .findFirst()
                .orElseThrow();
    }

//    Найдите в списке целых чисел 3-е наибольшее «уникальное» число
//    (пример: 5 2 10 9 4 3 10 1 13 => 9, в отличие от прошлой задачи здесь разные 10 считает за одно число)
    public static int thirdUniqueMax(List<Integer> numbers) {
        return numbers.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(2)
                .findFirst()
                .orElseThrow();
    }

//    Имеется список объектов типа Сотрудник (имя, возраст, должность),
//    необходимо получить список имен 3 самых старших сотрудников с должностью «Инженер»,
//    в порядке убывания возраста
    public static List<String> top3EngineersByAge(List<Employee> employees) {
        return employees.stream()
                .filter(e -> "Инженер".equals(e.getPosition()))
                .sorted(Comparator.comparingInt(Employee::getAge).reversed())
                .limit(3)
                .map(Employee::getName)
                .toList();
    }

//    Имеется список объектов типа Сотрудник (имя, возраст, должность),
//    посчитайте средний возраст сотрудников с должностью «Инженер»
    public static double averageAgeOfEngineers(List<Employee> employees) {
        return employees.stream()
                .filter(e -> "Инженер".equals(e.getPosition()))
                .mapToInt(Employee::getAge)
                .average()
                .orElse(0);
    }

//    Найдите в списке слов самое длинное
    public static String longestWord(List<String> words) {
        return words.stream()
                .max(Comparator.comparingInt(String::length))
                .orElseThrow();
    }

//    Имеется строка с набором слов в нижнем регистре, разделенных пробелом. Постройте хеш-мапы,
//    в которой будут храниться пары: слово - сколько раз оно встречается во входной строке
    public static Map<String, Long> wordFrequency(String text) {
        return Arrays.stream(text.split("\\s+"))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

//    Отпечатайте в консоль строки из списка в порядке увеличения длины слова,
//    если слова имеют одинаковую длины, то должен быть сохранен алфавитный порядок
    public static List<String> sortByLengthThenAlphabet(List<String> words) {
        return words.stream()
                .sorted(Comparator.comparingInt(String::length)
                        .thenComparing(Comparator.naturalOrder()))
                .toList();
    }

//    Имеется массив строк, в каждой из которых лежит набор из 5 слов,
//    разделенных пробелом, найдите среди всех слов самое длинное,
//    если таких слов несколько, получите любое из них
    public static String longestWordFromArray(String[] lines) {
        return Arrays.stream(lines)
                .flatMap(line -> Arrays.stream(line.split("\\s+")))
                .max(Comparator.comparingInt(String::length))
                .orElseThrow();
    }
}

