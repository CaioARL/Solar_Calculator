package com.example.solarcalculator.Dto;

public class BoundingBoxDTO {
    private String minLatitude;
    private String maxLatitude;
    private String minLongitude;
    private String maxLongitude;

    public String getMinLatitude() {
        return minLatitude;
    }

    public void setMinLatitude(String minLatitude) {
        this.minLatitude = minLatitude;
    }

    public String getMaxLatitude() {
        return maxLatitude;
    }

    public void setMaxLatitude(String maxLatitude) {
        this.maxLatitude = maxLatitude;
    }

    public String getMinLongitude() {
        return minLongitude;
    }

    public void setMinLongitude(String minLongitude) {
        this.minLongitude = minLongitude;
    }

    public String getMaxLongitude() {
        return maxLongitude;
    }

    public void setMaxLongitude(String maxLongitude) {
        this.maxLongitude = maxLongitude;
    }
}