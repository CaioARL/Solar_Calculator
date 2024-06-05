package com.example.solarcalculator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CordenadasDTO {
    private String latitude;
    private String longitude;

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
