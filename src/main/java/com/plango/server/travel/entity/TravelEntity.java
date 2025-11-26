package com.plango.server.travel.entity;

import com.plango.server.travel.dto.CompanionType;
import com.plango.server.travel.dto.TravelType;
import com.plango.server.user.UserEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.ListIndexBase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA travel trable.
 */
@Entity
@Table(name = "travel")
public class TravelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "travel_id")
    private Long travelId;

    // 다대일(N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)  // FK 컬럼명 그대로 매핑
    private UserEntity user;

    @Column(name = "travel_dest", nullable = false, length = 100)
    private String travelDest;

    @Column(name = "travel_start", nullable = false)
    private LocalDate travelStart;

    @Column(name = "travel_end", nullable = false)
    private LocalDate travelEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "travel_type", nullable = false)
    private TravelType travelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "companion_type", nullable = false)
    private CompanionType companionType;

    @Column(name = "travel_theme1", length = 20, nullable = false)
    private String travelTheme1;

    @Column(name = "travel_theme2", length = 20, nullable = false)
    private String travelTheme2;

    @Column(name = "travel_theme3", length = 20, nullable = false)
    private String travelTheme3;

    @Generated(GenerationTime.INSERT)
    @ColumnDefault("CURRENT_DATE")
    @Column(name = "created_date", nullable = false, updatable = false, insertable = false)
    private LocalDate createdDate;

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("travelDayIndex ASC")
    @OrderColumn(name = "travel_day_index")
    @ListIndexBase(1)
    private final List<TravelDayEntity> travelDays = new ArrayList<TravelDayEntity>();

    // 기본 생성자
    protected TravelEntity() {
    }

    public TravelEntity(UserEntity user, String travelDest, LocalDate travelStart, LocalDate travelEnd,
                        TravelType travelType, CompanionType companionType,
                        String travelTheme1, String travelTheme2, String travelTheme3) {
        this.user = user;
        this.travelDest = travelDest;
        this.travelStart = travelStart;
        this.travelEnd = travelEnd;
        this.travelType = travelType;
        this.companionType = companionType;
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

    public LocalDate getTravelStart() {
        return travelStart;
    }

    public LocalDate getTravelEnd() {
        return travelEnd;
    }

    public TravelType getTravelType() {
        return travelType;
    }

    public CompanionType getCompanionType() {
        return companionType;
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

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public List<TravelDayEntity> getTravelDays() {
        return travelDays;
    }
}
