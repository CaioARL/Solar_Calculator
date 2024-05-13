package com.example.solarcalculator.Dto;

public class CordenadasDTO {
    private String latitude;
    private String longitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public CordenadasDTO getCordenadasToSearch(){
        this.setLatitude(this.latitude.substring(0,6));
        this.setLongitude(this.longitude.substring(0,7));
        return this;
    }

    public CordenadasDTO(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
