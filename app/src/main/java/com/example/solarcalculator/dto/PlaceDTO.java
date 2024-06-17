package com.example.solarcalculator.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceDTO {
    @SerializedName("place_id")
    private int placeId;
    private String licence;
    @SerializedName("osm_type")
    private String osmType;
    @SerializedName("osm_id")
    private String osmId;
    private String lat;
    private String lon;
    private String category;
    private String type;
    @SerializedName("place_rank")
    private int placeRank;
    private double importance;
    @SerializedName("addresstype")
    private String addressType;
    private String name;
    @SerializedName("display_name")
    private String displayName;
    private List<String> boundingbox;
}