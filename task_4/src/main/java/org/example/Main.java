package org.example;

import org.example.domain.User;
import org.example.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Точка входа в приложение.
 * Загружает Spring-контекст и демонстрирует работу UserService.
 */
@ComponentScan(basePackages = "org.example")
public class Main {
    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(Main.class)) {
            DataSource ds = ctx.getBean(DataSource.class);
            try (Connection conn = ds.getConnection();
                 Statement st = conn.createStatement()) {
                st.execute("TRUNCATE TABLE app_data.users RESTART IDENTITY");
            }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
