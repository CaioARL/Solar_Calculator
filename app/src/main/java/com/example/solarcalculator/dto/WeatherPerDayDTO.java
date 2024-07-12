package com.example.solarcalculator.dto;

import androidx.annotation.NonNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherPerDayDTO {

    private String date;
    private String temperature;
    private String precipitation;
    private String cloudCover;
    private String windSpeed;

    @NonNull
    public String toString() {
    return date + "\n" +
            "Temperatura: " + temperature + "\n" +
            "Precipitação: " + precipitation + "\n" +
            "Nuvens: " + cloudCover + "\n" +
            "Velocidade do vento: " + windSpeed + "\n\n";
    }
}
