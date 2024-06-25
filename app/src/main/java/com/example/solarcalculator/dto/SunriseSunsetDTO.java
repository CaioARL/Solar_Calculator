package com.example.solarcalculator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SunriseSunsetDTO {
    Results results;
    String status;
    String tzid;
    double exposureHours;

    @Getter
    @Setter
    public static class Results {
        private String sunrise;
        private String sunset;
        private String solar_noon;
        private Long day_length;
        private String civil_twilight_begin;
        private String civil_twilight_end;
        private String nautical_twilight_begin;
        private String nautical_twilight_end;
        private String astronomical_twilight_begin;
        private String astronomical_twilight_end;
    }
}