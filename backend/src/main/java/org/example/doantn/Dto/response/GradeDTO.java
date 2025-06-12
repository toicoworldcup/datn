package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GradeDTO {
    private String mssv;
    private String name;
    private String maLop;
    private Double gki;
    private Double cki;
    private String history;
    private String nameCourse;

    public GradeDTO(Double cki, Double gki, String maLop, String mssv, String name, String history,String nameCourse) {
        this.cki = cki;
        this.gki = gki;
        this.maLop = maLop;
        this.mssv = mssv;
        this.name = name;
        this.history = history;
        this.nameCourse = nameCourse;
    }

}