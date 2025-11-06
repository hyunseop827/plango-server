package com.plango.server.travel;

import com.plango.server.travel.dto.CompanionType;
import com.plango.server.travel.dto.TravelType;
import com.plango.server.user.UserEntity;
import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "travels")
public class TravelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "travel_id")
    private Long travelId;

    // 다대일(N:1) 관계 — 여러 여행이 한 유저에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)  // FK 컬럼명 그대로 매핑
    private UserEntity user;

    @Column(name = "travel_dest", nullable = false, length = 100)
    private String travelDest;

    @Column(name = "travel_start", nullable = false)
    private Date travelStart;

    @Column(name = "travel_end", nullable = false)
    private Date travelEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "travel_type", nullable = false)
    private TravelType travelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "travel_companion", nullable = false)
    private CompanionType travelCompanion;

    @Column(name = "travel_theme1", length = 20)
    private String travelTheme1;

    @Column(name = "travel_theme2", length = 20)
    private String travelTheme2;

    @Column(name = "travel_theme3", length = 20)
    private String travelTheme3;

    // 기본 생성자
    protected TravelEntity() {
    }

    public TravelEntity(UserEntity user, String travelDest, Date travelStart, Date travelEnd,
                        TravelType travelType, CompanionType travelCompanion,
                        String travelTheme1, String travelTheme2, String travelTheme3) {
        this.user = user;
        this.travelDest = travelDest;
        this.travelStart = travelStart;
        this.travelEnd = travelEnd;
        this.travelType = travelType;
        this.travelCompanion = travelCompanion;
        this.travelTheme1 = travelTheme1;
        this.travelTheme2 = travelTheme2;
        this.travelTheme3 = travelTheme3;
    }

    public Long getTravelId() {
        return travelId;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getTravelDest() {
        return travelDest;
    }

    public Date getTravelStart() {
        return travelStart;
    }

    public Date getTravelEnd() {
        return travelEnd;
    }

    public TravelType getTravelType() {
        return travelType;
    }

    public CompanionType getTravelCompanion() {
        return travelCompanion;
    }

    public String getTravelTheme1() {
        return travelTheme1;
    }

    public String getTravelTheme2() {
        return travelTheme2;
    }

    public String getTravelTheme3() {
        return travelTheme3;
    }
}
