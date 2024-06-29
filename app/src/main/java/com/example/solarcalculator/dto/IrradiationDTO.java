package com.example.solarcalculator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IrradiationDTO {
    private double latitude;
    private double longitude;
    private double generationtime_ms;
    private int utc_offset_seconds;
    private String timezone;
    private String timezone_abbreviation;
    private double elevation;
    private HourlyUnitsDTO hourly_units;
    private HourlyDTO hourly;
}


