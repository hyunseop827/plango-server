package com.plango.server.travel.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ListIndexBase;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA travel day table
 */
@Entity
@Table(name = "travel_day",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_travel_day_unique",
                columnNames = {"travel_id", "travel_day_index"}
        ))
public class TravelDayEntity {
    @OneToMany(mappedBy = "travelDay", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("locationOrder ASC")
    @OrderColumn(name = "location_order")
    @ListIndexBase(1)
    private final List<TravelCourseEntity> courses = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "travel_day_id")
    private Long travelDayId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", nullable = false)
    private TravelEntity travel;
    @Column(name = "travel_day_index", nullable = false)
    private int travelDayIndex;

    public TravelDayEntity(TravelEntity travel, int travelDayIndex) {
        this.travel = travel;
        this.travelDayIndex = travelDayIndex;
    }

    protected TravelDayEntity() {
    }

    public Long getTravelDayId() {
        return travelDayId;
    }

    public TravelEntity getTravel() {
        return travel;
    }

    public int getTravelDayIndex() {
        return travelDayIndex;
    }

    public List<TravelCourseEntity> getCourses() {
        return courses;
    }
}
