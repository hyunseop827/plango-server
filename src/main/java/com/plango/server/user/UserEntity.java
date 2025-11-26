package com.plango.server.user;

import com.plango.server.travel.entity.TravelEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA user table.
 */
@Entity
@Table(name = "user",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_public_id",
                columnNames = "user_public_id"
        ))
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_public_id", nullable = false, length = 36)
    private String publicId;

    @Column(name = "user_nickname", nullable = false, length = 15)
    private String nickname;

    @Column(name = "user_mbti", nullable = false, length = 4)
    private String mbti;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<TravelEntity> travels = new ArrayList<>();

    public UserEntity() {
    }

    public UserEntity(String publicId, String nickname, String mbti) {
        this.publicId = publicId;
        this.nickname = nickname;
        this.mbti = mbti;
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMbti() {
        return mbti;
    }
}
