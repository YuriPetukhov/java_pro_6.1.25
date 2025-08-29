package org.example.bootstrap;

import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Демонстрационный раннер CRUD-операций.
 */
@Component
@Profile("demo")
public class DemoDataRunner implements CommandLineRunner {

    private final UserService service;

    public DemoDataRunner(UserService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        User a = service.createUser("alice");
        User b = service.createUser("bob");

        System.out.println("Created or existing: " + a + " / " + b);
        System.out.println("All: " + service.getAll());

        service.rename(b.getId(), "bob2");
        System.out.println("Get b: " + service.getUser(b.getId()));

        service.delete(a.getId());
        System.out.println("All after delete: " + service.getAll());
    }
}
