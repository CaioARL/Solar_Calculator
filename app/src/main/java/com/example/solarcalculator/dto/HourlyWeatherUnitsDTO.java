package com.example.solarcalculator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HourlyWeatherUnitsDTO {
    private String time;
    private String temperature_2m;
    private String precipitation;
    private String cloud_cover;
    private String wind_speed_10m;
}
