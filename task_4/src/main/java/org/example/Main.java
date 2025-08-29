package org.example;

import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main implements CommandLineRunner {

    private final UserService service;

    public Main(UserService service) {
        this.service = service;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        User user1 = service.createUser("alice");
        User user2 = service.createUser("bob");

        System.out.println("Created: " + user1 + " / " + user2);
        System.out.println("All: " + service.getAll());

        service.rename(user2.getId(), "bob2");
        System.out.println("Get b: " + service.getUser(user2.getId()));

        service.delete(user1.getId());
        System.out.println("All after delete: " + service.getAll());
    }
}
