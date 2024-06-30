package com.example.solarcalculator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HourlyIrradiationUnitsDTO {
    private String time;
    private String shortwave_radiation;
    private String direct_radiation;
    private String diffuse_radiation;
    private String direct_normal_irradiance;
    private String global_tilted_irradiance;
    private String terrestrial_radiation;
}
