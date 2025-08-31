package org.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String username;

    protected User() {}
    public User(String username) { this.username = username; }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override public String toString() {
        return "User[id=%s, username=%s]".formatted(id, username);
    }
}
