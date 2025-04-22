package org.example.doantn.Dto.response;

import org.example.doantn.Entity.Dangkihocphan;

public class DkhpDTO {
    private String maHocPhan;
    private String semesterName;
    private String mssv;

    public DkhpDTO(String maHocPhan, String mssv, String semesterName) {
        this.maHocPhan = maHocPhan;
        this.mssv = mssv;
        this.semesterName = semesterName;
    }

    public DkhpDTO() {
    }
    public DkhpDTO(Dangkihocphan dangkihocphan) {
        this.maHocPhan = dangkihocphan.getCourse().getMaHocPhan();
        this.mssv = dangkihocphan.getStudent().getMssv();
        this.semesterName = dangkihocphan.getSemester().getName();
    }

    public String getMaHocPhan() {
        return maHocPhan;
    }

    public void setMaHocPhan(String maHocPhan) {
        this.maHocPhan = maHocPhan;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }
}
