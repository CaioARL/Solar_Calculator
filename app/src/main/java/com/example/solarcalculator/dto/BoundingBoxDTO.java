package com.example.solarcalculator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoundingBoxDTO {
    private String minLatitude;
    private String maxLatitude;
    private String minLongitude;
    private String maxLongitude;
}