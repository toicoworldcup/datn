package org.example.doantn.Dto.response;

import org.example.doantn.Entity.Grade;

public class GradeDTO {
    private String mssv;
    private String name;
    private String maLop;
    private Double gki;
    private Double cki;

    public GradeDTO() {
    }

    public GradeDTO(Double cki, Double gki,String maLop, String mssv, String name) {
        this.cki = cki;
        this.gki = gki;
        this.maLop = maLop;
        this.mssv = mssv;
        this.name = name;
    }
    public GradeDTO(Grade grade) {
        this.cki = grade.getDiemCk();
        this.gki = grade.getDiemGk();
        this.maLop = grade.getClazz().getMaLop();
        this.mssv = grade.getStudent().getMssv();
        this.name = grade.getStudent().getName();
    }

    public Double getCki() {
        return cki;
    }

    public void setCki(Double cki) {
        this.cki = cki;
    }

    public Double getGki() {
        return gki;
    }

    public void setGki(Double gki) {
        this.gki = gki;
    }


    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
