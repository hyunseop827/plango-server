package com.plango.server.user;

import com.plango.server.travel.TravelEntity;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "users", // 테이블 이름 및 유니크 제약 명시적
        uniqueConstraints = @UniqueConstraint(
                name = "uq_users_public_id",
                columnNames = "user_public_id"
        ))
public class UserEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="user_id") private Long id; // 내부 PK

    @Column(name="user_public_id", nullable = false,length = 36)
    private String publicId;

    @Column(name="user_nickname", nullable = false,length = 15)
    private String nickname;

    @Column(name="user_mbti",nullable = false,length = 4)
    private String mbti;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelEntity> travels = new ArrayList<>();

    public UserEntity(String publicId,
                      String nickname, String mbti) {
        this.publicId = publicId;
        this.nickname = nickname;
        this.mbti = mbti;
    }

    public UserEntity() { }

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
