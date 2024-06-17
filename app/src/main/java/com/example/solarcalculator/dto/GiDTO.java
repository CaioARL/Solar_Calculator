package com.example.solarcalculator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
