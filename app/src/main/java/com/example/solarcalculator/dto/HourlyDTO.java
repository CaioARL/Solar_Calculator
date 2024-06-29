package com.example.solarcalculator.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HourlyDTO {
    private List<String> time;
    private List<Double> shortwave_radiation;
    private List<Double> direct_radiation;
    private List<Double> diffuse_radiation;
    private List<Double> direct_normal_irradiance;
    private List<Double> global_tilted_irradiance;
    private List<Double> terrestrial_radiation;
}