package org.example.tests;

import org.example.annotations.*;

public class BasicTestSuite {

    @BeforeSuite
    public static void setup() {
        System.out.println("[BeforeSuite] Global test setup");
    }

    @AfterSuite
    public static void cleanup() {
        System.out.println("[AfterSuite] Global test cleanup");
    }

    @BeforeTest
    public void beforeEach() {
        System.out.println("[BeforeTest] Preparing for test");
    }

    @AfterTest
    public void afterEach() {
        System.out.println("[AfterTest] Cleaning up after test");
    }

    @Test(priority = 10)
    public void highPriorityTest() {
        System.out.println("[Test 10] High-priority logic executed");
    }

    @Test(priority = 1)
    public void lowPriorityTest() {
        System.out.println("[Test 1] Low-priority logic executed");
    }

    @Test
    public void defaultPriorityTest() {
        System.out.println("[Test 5] Default-priority logic executed");
    }
}
