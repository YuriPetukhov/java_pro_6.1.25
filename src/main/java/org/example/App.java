package org.example;

import org.example.runner.TestRunner;
import org.example.tests.BasicTestSuite;
import org.example.tests.CsvBasedTestSuite;

public class App {
    public static void main( String[] args ) {
        TestRunner.runTests(BasicTestSuite.class);
        TestRunner.runTests(CsvBasedTestSuite.class);
    }
}
