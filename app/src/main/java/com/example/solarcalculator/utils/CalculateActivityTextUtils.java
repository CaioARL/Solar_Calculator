package com.example.solarcalculator.utils;

import java.util.Locale;

public class CalculateActivityTextUtils {

    private static final Double sixtyPercent = 0.6;
    private static final Double twentyPercent = 0.2;
    private static final Double dollar = 5.5;
    private static final Double icms = (double) (20/100);

    Double energy;
    Double irradiation;
    Double energyOnCloudyDays;
    Double energyOnRainyDays;
    Double energyPrice;
    Integer period;
    Double co2ReductionCoal;
    Double co2ReductionNaturalGas;
    Double co2ReductionOil;
    Double co2ReductionElectricityMix;
    Double initialInvestment;
    Double economyYearly;
    Double payback;
    Double panelArea;
    Double panelEfficiency;
    Integer numberOfPanels;

    public CalculateActivityTextUtils(Double energy, float energyPrice, Double panelArea, Double panelEfficiency, Double irradiation, Integer numberOfPanels, Integer period) {
        this.energy = energy * numberOfPanels;
        this.panelArea = panelArea;
        this.irradiation = irradiation;
        this.numberOfPanels = numberOfPanels;
        this.panelEfficiency = panelEfficiency;
        this.energyOnCloudyDays = energy * sixtyPercent;
        this.energyOnRainyDays = energy * twentyPercent;
        this.energyPrice = (double) energyPrice;
        this.period = period;
        this.co2ReductionCoal = energy * 0.94;
        this.co2ReductionNaturalGas = energy * 0.56;
        this.co2ReductionOil = energy * 0.71;
        this.co2ReductionElectricityMix = energy * 0.5;
        this.initialInvestment = getInitialInvestment();
        this.economyYearly = getAnnualPrice();
        this.payback = initialInvestment/getAnnualPrice();
    }

    public String getEconomy() {
        return String.format(Locale.US,"Economia %s estimada na conta de luz: R$%.2f",
                getNamePeriod(), energy*energyPrice);
    }

    public String getROI() {
        return String.format(Locale.US,"Retorno sobre o investimento: " +
                "\nInvestimento inicial: R$%.2f" +
                "\nEconomia anual: R$%.2f" +
                "\nPayback: %.2f anos",
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
        return String.format(Locale.US,"Produção de energia em diferentes condições climáticas: " +
                "\nEnsolarado: %.2f kWh" +
                "\nParcialmente nublado: %.2f kWh" +
                "\nChuvoso: %.2f kWh",
                energy, energyOnCloudyDays, energyOnRainyDays);
    }

    public String getWeatherForecast() {
        return "Previsão do tempo: ";
    }

    public double getInitialInvestment() {
        return ((panelArea * (panelEfficiency/100) * irradiation * energyPrice * dollar) * 1 + icms) * numberOfPanels;
    }

    public Double getAnnualPrice() {
        if (period == 0) {
            return (energy * 365) * energyPrice;
        }
        if (period == 1) {
            return (energy * 12) * energyPrice;
        }
        if (period == 2) {
            return energy * energyPrice;
        }
        return 0.0;
    }

    public String getNamePeriod(){
        if (period == 0) {
            return "Diário";
        }
        if (period == 1) {
            return "Mensal";
        }
        if (period == 2) {
            return "Anual";
        }
        return "";
    }
}
