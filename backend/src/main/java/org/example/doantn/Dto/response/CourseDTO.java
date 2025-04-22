package org.example.doantn.Dto.response;

import org.example.doantn.Entity.Course;

public class CourseDTO {
    private String maHocPhan;
    private int soTinChi ;
    private String tenMonHoc;

    public CourseDTO() {
    }

    public CourseDTO(String maHocPhan, int soTinChi, String tenMonHoc) {
        this.maHocPhan = maHocPhan;
        this.soTinChi = soTinChi;
        this.tenMonHoc = tenMonHoc;
    }
    public CourseDTO(Course course) {
        this.maHocPhan = course.getMaHocPhan();
        this.soTinChi = course.getTinChi();
        this.tenMonHoc = course.getName();
    }

    public String getMaHocPhan() {
        return maHocPhan;
    }

    public void setMaHocPhan(String maHocPhan) {
        this.maHocPhan = maHocPhan;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.tenMonHoc = tenMonHoc;
    }
}
