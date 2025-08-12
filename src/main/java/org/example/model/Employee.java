package org.example.model;

public class Employee {
    private final String name;
    private final int age;
    private final String position;

    public Employee(String name, int age, String position) {
        this.name = name;
        this.age = age;
        this.position = position;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getPosition() { return position; }
}

