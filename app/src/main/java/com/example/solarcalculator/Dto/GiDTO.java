package com.example.solarcalculator.Dto;

public class GiDTO {

    private Long id;
    private String country;
    private String lon;
    private String lat;
    private Long annual;
    private Long jan;
    private Long fev;
    private Long mar;
    private Long abr;
    private Long mai;
    private Long jun;
    private Long jul;
    private Long ago;
    private Long set;
    private Long out;
    private Long nov;
    private Long dec;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public Long getAnnual() { return annual; }

    public void setAnnual(Long annual) { this.annual = annual; }

    public Long getJan() {
        return jan;
    }

    public void setJan(Long jan) {
        this.jan = jan;
    }

    public Long getFev() {
        return fev;
    }

    public void setFev(Long fev) {
        this.fev = fev;
    }

    public Long getMar() {
        return mar;
    }

    public void setMar(Long mar) {
        this.mar = mar;
    }

    public Long getAbr() {
        return abr;
    }

    public void setAbr(Long abr) {
        this.abr = abr;
    }

    public Long getMai() {
        return mai;
    }

    public void setMai(Long mai) {
        this.mai = mai;
    }

    public Long getJun() {
        return jun;
    }

    public void setJun(Long jun) {
        this.jun = jun;
    }

    public Long getJul() {
        return jul;
    }

    public void setJul(Long jul) {
        this.jul = jul;
    }

    public Long getAgo() {
        return ago;
    }

    public void setAgo(Long ago) {
        this.ago = ago;
    }

    public Long getSet() {
        return set;
    }

    public void setSet(Long set) {
        this.set = set;
    }

    public Long getOut() {
        return out;
    }

    public void setOut(Long out) {
        this.out = out;
    }

    public Long getNov() {
        return nov;
    }

    public void setNov(Long nov) {
        this.nov = nov;
    }

    public Long getDec() {
        return dec;
    }

    public void setDec(Long dec) {
        this.dec = dec;
    }

    public Long getValueOfMonth(int mes) {
        switch(mes) {
            case 1:
                return this.getJan();
            case 2:
                return this.getFev();
            case 3:
                return this.getMar();
            case 4:
                return this.getAbr();
            case 5:
                return this.getMai();
            case 6:
                return this.getJun();
            case 7:
                return this.getJul();
            case 8:
                return this.getAgo();
            case 9:
                return this.getSet();
            case 10:
                return this.getOut();
            case 11:
                return this.getNov();
            case 12:
                return this.getDec();
            default:
                throw new IllegalArgumentException("Mês inválido: " + mes);
        }
    }
}
