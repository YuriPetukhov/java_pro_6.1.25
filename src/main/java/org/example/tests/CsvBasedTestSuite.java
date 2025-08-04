package org.example.tests;

import org.example.annotations.CsvSource;
import org.example.annotations.Test;

public class CsvBasedTestSuite {

    @Test(priority = 7)
    @CsvSource("42, Hello, 3.14, true")
    public void testWithCsv(int a, String b, double c, boolean d) {
        System.out.printf("[Test with Csv] Parsed values: %d, %s, %.2f, %b\n", a, b, c, d);
    }

    @Test(priority = 3)
    @CsvSource("100,World,2.71,false")
    public void anotherCsvTest(int x, String y, double z, boolean f) {
        System.out.printf("[Test with Csv] Another values: %d, %s, %.2f, %b\n", x, y, z, f);
    }
}
