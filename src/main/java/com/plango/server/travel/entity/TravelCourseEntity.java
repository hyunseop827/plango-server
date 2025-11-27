package com.plango.server.travel.entity;

import jakarta.persistence.*;

/**
 * JPA travel course table.
 */
@Entity
@Table(name = "travel_course")
public class TravelCourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="travel_course_id")
    private Long travelCourseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="travel_day_id",nullable = false)
    private TravelDayEntity travelDay;

    @Column(name ="location_name",nullable = false,length = 100)
    private String locationName;

    @Column(name="location_lat")
    private Double locationLat;

    @Column(name="location_lng")
    private Double locationLng;

    @Column(name="location_note",length = 100)
    private String locationNote;

    @Column(name="location_theme",length=10)
    private String locationTheme;

    @Column(name="location_time")
    private Integer locationTime;

    @Column(name = "location_order")
    private int locationOrder;

    public TravelCourseEntity(
            TravelDayEntity day,
            String locationName,
            Double lat, Double lng,
            String note, String theme,
            Integer howLong,
            int order
    ) {
        this.travelDay = day;
        this.locationName = locationName;
        this.locationLat = lat;
        this.locationLng = lng;
        this.locationNote = note;
        this.locationTheme = theme;
        this.locationTime = howLong;
        this.locationOrder = order;
    }

    public Long getTravelCourseId() {
        return travelCourseId;
    }

    public TravelDayEntity getTravelDay() {
        return travelDay;
    }

    public String getLocationName() {
        return locationName;
    }

    public Double getLocationLat() {
        return locationLat;
    }

    public Double getLocationLng() {
        return locationLng;
    }

    public String getLocationNote() {
        return locationNote;
    }

    public String getLocationTheme() {
        return locationTheme;
    }

    public Integer getLocationTime() {
        return locationTime;
    }

    public int getLocationOrder() {
        return locationOrder;
    }

    protected TravelCourseEntity() {  }


    public void setLocationLat(Double locationLat) {
        this.locationLat = locationLat;
    }

    public void setLocationLng(Double locationLng) {
        this.locationLng = locationLng;
    }
}
