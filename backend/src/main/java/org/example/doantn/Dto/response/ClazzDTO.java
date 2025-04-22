package org.example.doantn.Dto.response;

import org.example.doantn.Entity.Clazz;

import java.time.LocalDate;

public class ClazzDTO {
    private String maLop;
    private String maHocPhan;
    private String hocki;
    private LocalDate lichThi;


    public ClazzDTO(Clazz clazz) {
        this.hocki = clazz.getSemester().getName();
        this.lichThi = LocalDate.now();
        this.maHocPhan = clazz.getCourse().getMaHocPhan();
        this.maLop = clazz.getMaLop();
    }

    public ClazzDTO(String maLop,String hocki, String maHocPhan, LocalDate lichThi) {
        this.hocki = hocki;
        this.lichThi = lichThi;
        this.maHocPhan = maHocPhan;
        this.maLop = maLop;
    }

    public ClazzDTO() {
    }


    public String getHocki() {
        return hocki;
    }

    public void setHocki(String hocki) {
        this.hocki = hocki;
    }

    public LocalDate getLichThi() {
        return lichThi;
    }

    public void setLichThi(LocalDate lichThi) {
        this.lichThi = lichThi;
    }

    public String getMaHocPhan() {
        return maHocPhan;
    }

    public void setMaHocPhan(String maHocPhan) {
        this.maHocPhan = maHocPhan;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }
}
