package com.plango.server.user;

import jakarta.persistence.*;

@Entity @Table(name="users")
public class UserEntity {
    @Id
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String mbti;

    public UserEntity(String id, String name, String mbti) {
        this.id = id;
        this.name = name;
        this.mbti = mbti;
    }
    protected UserEntity() {}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMbti() {
        return mbti;
    }
}
