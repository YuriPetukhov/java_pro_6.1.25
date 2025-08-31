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
        User user1 = service.createUser("alice");
        User user2 = service.createUser("bob");

        System.out.println("Created or existing: " + user1 + " / " + user2);
        System.out.println("All: " + service.getAll());

        service.rename(user2.getId(), "bob2");
        System.out.println("Get user2: " + service.getUser(user2.getId()));

        service.delete(user1.getId());
        System.out.println("All after delete: " + service.getAll());
    }
}
