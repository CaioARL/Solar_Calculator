package com.example.solarcalculator.utils;

import androidx.annotation.NonNull;

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
        return "Economia " + getNamePeriod() + " estimada na conta de luz: R$" + energy*energyPrice;
    }

    public String getROI() {
        return "Retorno sobre o investimento: " +
                "\nInvestimento inicial: R$" + initialInvestment +
                "\nEconomia anual: R$" + economyYearly +
                "\nPayback: " + payback + " anos";
    }

    public String getCO2Reduction() {
        return "Redução de CO2: " +
                "\nCarvão: " + co2ReductionCoal + " kg" +
                "\nGás natural: " + co2ReductionNaturalGas + " kg" +
                "\nÓleo: " + co2ReductionOil + " kg" +
                "\nMix de eletricidade: " + co2ReductionElectricityMix + " kg";
    }

    public String getWeatherImpactEnergyProduction() {
        return "Produção de energia em diferentes condições climáticas: " +
                "\nEnsolarado: " + energy + " kWh" +
                "\nParcialmente nublado: " + energyOnCloudyDays + " kWh" +
                "\nChuvoso: " + energyOnRainyDays + " kWh";
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
