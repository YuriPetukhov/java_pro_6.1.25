package org.example.service;

import org.example.model.Employee;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class StreamTasksTest {

    @Test
    void testThirdMax() {
        assertEquals(10, StreamTasks.thirdMax(List.of(5, 2, 10, 9, 4, 3, 10, 1, 13)));
    }

    @Test
    void thirdMax_tooFew_throws() {
        assertThrows(NoSuchElementException.class,
                () -> StreamTasks.thirdMax(List.of(1, 2)));
    }


    @Test
    void testThirdUniqueMax() {
        assertEquals(9, StreamTasks.thirdUniqueMax(List.of(5, 2, 10, 9, 4, 3, 10, 1, 13)));
    }

    @Test
    void thirdUniqueMax_notEnoughUnique_throws() {
        assertThrows(NoSuchElementException.class,
                () -> StreamTasks.thirdUniqueMax(List.of(7, 7)));
    }


    @Test
    void testTop3EngineersByAge() {
        List<Employee> employees = List.of(
                new Employee("Иван", 30, "Инженер"),
                new Employee("Петр", 40, "Инженер"),
                new Employee("Сергей", 35, "Инженер"),
                new Employee("Анна", 50, "Менеджер")
        );
        assertEquals(List.of("Петр", "Сергей", "Иван"), StreamTasks.top3EngineersByAge(employees));
    }

    @Test
    void testAverageAgeOfEngineers() {
        List<Employee> employees = List.of(
                new Employee("Иван", 30, "Инженер"),
                new Employee("Петр", 40, "Инженер"),
                new Employee("Анна", 50, "Менеджер")
        );
        assertEquals(35.0, StreamTasks.averageAgeOfEngineers(employees));
    }

    @Test
    void testLongestWord() {
        assertEquals("программирование", StreamTasks.longestWord(List.of("код", "программирование", "java")));
    }
    @Test
    void longestWord_emptyList_throws() {
        assertThrows(NoSuchElementException.class,
                () -> StreamTasks.longestWord(List.of()));
    }


    @Test
    void testWordFrequency() {
        assertEquals(Map.of("java", 2L, "stream", 1L),
                StreamTasks.wordFrequency("java stream java"));
    }

    @Test
    void testSortByLengthThenAlphabet() {
        assertEquals(List.of("aa", "ab", "bca", "zzz"),
                StreamTasks.sortByLengthThenAlphabet(List.of("zzz", "aa", "bca", "ab")));
    }

    @Test
    void testLongestWordFromArray() {
        String[] lines = {
                "java code stream api",
                "functional programming task"
        };
        assertEquals("programming", StreamTasks.longestWordFromArray(lines));
    }

    @Test
    void longestWordFromArray_noWords_throws() {
        assertThrows(NoSuchElementException.class,
                () -> StreamTasks.longestWordFromArray(new String[]{}));
    }

}