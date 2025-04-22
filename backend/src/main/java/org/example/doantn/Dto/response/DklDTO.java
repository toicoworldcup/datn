package org.example.doantn.Dto.response;

import org.example.doantn.Entity.Dangkilop;

public class DklDTO {
    private String maLop;
    private String semesterName;
    public DklDTO(String maLop, String semesterName) {
        this.maLop = maLop;
        this.semesterName = semesterName;
    }

    public DklDTO() {
    }
    public DklDTO(Dangkilop dangkilop) {
        this.maLop = dangkilop.getClazz().getMaLop();
        this.semesterName = dangkilop.getSemester().getName();
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }
}

