package com.example.solarcalculator.utils;

import com.example.solarcalculator.dto.WeatherDTO;
import com.example.solarcalculator.dto.WeatherPerDayDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CalculateActivityTextUtils {

    private static final Double twentyPercent = 0.2;
    private static final Double fiftyPercent = 0.5;
    private static final Double seventyPercent = 0.7;
    private static final Double ninetyPercent = 0.9;
    private static final Double dollar = 5.5;

    Double energy;
    Double irradiation;
    Double energyPrice;
    Integer period;
    Double co2ReductionCoal;
    Double co2ReductionNaturalGas;
    Double co2ReductionOil;
    Double co2ReductionElectricityMix;
    Double initialInvestment;
    Double economyYearly;
    long payback;
    Double panelArea;
    Double panelEfficiency;
    Integer numberOfPanels;
    WeatherDTO weatherDTO;

    public CalculateActivityTextUtils(Double energy, float energyPrice, Double panelArea, Double panelEfficiency,
                                      Double irradiation, Integer numberOfPanels, Integer period, WeatherDTO weatherDTO) {
        this.energy = energy;
        this.panelArea = panelArea;
        this.irradiation = irradiation;
        this.numberOfPanels = numberOfPanels;
        this.panelEfficiency = panelEfficiency/100;
        this.energyPrice = (double) energyPrice;
        this.period = period;
        this.co2ReductionCoal = energy * ninetyPercent;
        this.co2ReductionNaturalGas = energy * fiftyPercent;
        this.co2ReductionOil = energy * seventyPercent;
        this.co2ReductionElectricityMix = energy * fiftyPercent;
        this.initialInvestment = getInitialInvestment();
        this.economyYearly = getAnnualPrice();
        this.payback = Math.round(initialInvestment/getAnnualPrice());
        this.weatherDTO = weatherDTO;
    }

    public String getEconomy() {
        return String.format(Locale.US,"Economia %s estimada na conta de luz: R$%.2f",
                getNamePeriod(), energy*energyPrice);
    }

    public String getROI() {
        return String.format(Locale.US,"Retorno sobre o investimento: " +
                "\nInvestimento inicial: R$%.2f" +
                "\nEconomia anual: R$%.2f" +
                    "\nPayback: %s anos",
                initialInvestment, economyYearly, payback);
    }

    public String getCO2Reduction() {
        return String.format(Locale.US,"Redução de CO2: " +
                "\nCarvão: %.2f kg" +
                "\nGás natural: %.2f kg" +
                "\nÓleo: %.2f kg" +
                "\nMix de eletricidade: %.2f kg",
                co2ReductionCoal, co2ReductionNaturalGas, co2ReductionOil, co2ReductionElectricityMix);
    }

    public String getWeatherImpactEnergyProduction() {
        return String.format(Locale.US,"Produção média de energia diária: %.2f kW/h",
                period==0 ? energy : energy/30);
    }

    public String getWeatherForecast() {
        return "Previsão do tempo: \n" + getWeather().toString()
                .replace("[", "")
                .replace("]", "")
                .replace(",", "");
    }

    public double getInitialInvestment() {
        return (panelArea * 2000) * dollar * panelEfficiency * numberOfPanels * (1+twentyPercent);
    }

    public Double getAnnualPrice() {
        if (period == 0) {
            return (energy * 365) * energyPrice;
        }
        if (period == 1) {
            return (energy * 12) * energyPrice;
        }
        return 0.0;
    }

    public String getNamePeriod(){
        if (period == 0) {
            return "diária";
        }
        if (period == 1) {
            return "mensal";
        }
        return "";
    }

    private List<WeatherPerDayDTO> getWeather(){
        List<WeatherPerDayDTO> weatherPerDaysDTO = new ArrayList<>();

        for(int i = 0; i < weatherDTO.getHourly().getTime().size(); i++){
            WeatherPerDayDTO weatherPerDay = new WeatherPerDayDTO();
            weatherPerDay.setDate(weatherDTO.getHourly().getTime().get(i));
            weatherPerDay.setTemperature(weatherDTO.getHourly().getTemperature_2m().get(i) + weatherDTO.getHourly_units().getTemperature_2m());
            weatherPerDay.setPrecipitation(weatherDTO.getHourly().getPrecipitation().get(i) + weatherDTO.getHourly_units().getPrecipitation());
            weatherPerDay.setCloudCover(weatherDTO.getHourly().getCloud_cover().get(i) + weatherDTO.getHourly_units().getCloud_cover());
            weatherPerDay.setWindSpeed(weatherDTO.getHourly().getWind_speed_10m().get(i) + weatherDTO.getHourly_units().getWind_speed_10m());
            weatherPerDaysDTO.add(weatherPerDay);
        }

        return weatherPerDaysDTO;
    }
}
