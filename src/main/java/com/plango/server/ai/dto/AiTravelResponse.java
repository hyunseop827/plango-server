package com.plango.server.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Returned specific travel plan from AI.
 * Based on user preferences.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiTravelResponse {

    private List<AiDay> days;

    public AiTravelResponse() {
    }

    public List<AiDay> getDays() {
        return days;
    }

    public void setDays(List<AiDay> days) {
        this.days = days;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AiDay {

        private List<AiCourse> courses;

        public AiDay() {
        }

        public List<AiCourse> getCourses() {
            return courses;
        }

        public void setCourses(List<AiCourse> courses) {
            this.courses = courses;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AiCourse {

        private Integer order;
        private String locationName;
        private Double lat;
        private Double lng;
        private String note;
        private String theme;
        private Integer howLong;

        public AiCourse() {
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getTheme() {
            return theme;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }

        public Integer getHowLong() {
            return howLong;
        }

        public void setHowLong(Integer howLong) {
            this.howLong = howLong;
        }
    }
}
