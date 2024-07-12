package com.example.solarcalculator.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SunriseSunsetDTO {
    Results results;
    String status;
    String tzid;

    @Getter
    @Setter
    public static class Results {
        private Date sunrise;
        private Date sunset;
        private Date solar_noon;
        private Long day_length;
        private Date civil_twilight_begin;
        private Date civil_twilight_end;
        private Date nautical_twilight_begin;
        private Date nautical_twilight_end;
        private Date astronomical_twilight_begin;
        private Date astronomical_twilight_end;
    }
}