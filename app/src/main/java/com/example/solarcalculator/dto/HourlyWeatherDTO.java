package com.example.solarcalculator.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HourlyWeatherDTO {
    private List<String> time;
    private List<Double> temperature_2m;
    private List<Double> precipitation;
    private List<Double> cloud_cover;
    private List<Double> wind_speed_10m;
}