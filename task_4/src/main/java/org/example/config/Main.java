package org.example.config;

import org.example.domain.User;
import org.example.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Демонстрационный запуск приложения без тестов.
 * Показывает работу {@link UserService} в консольном режиме.
 */
public class Main {
    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
            var svc = ctx.getBean(UserService.class);

            User a = svc.createUser("alice");
            User b = svc.createUser("bob");
            System.out.println("Created: " + a + " / " + b);

            System.out.println("Get a: " + svc.getUser(a.id()));
            System.out.println("All: " + svc.getAll());

            System.out.println("Rename bob -> bob2: " + svc.rename(b.id(), "bob2"));
            System.out.println("Get b: " + svc.getUser(b.id()));

            System.out.println("Delete a: " + svc.delete(a.id()));
            System.out.println("All after delete: " + svc.getAll());
        }
    }
}
