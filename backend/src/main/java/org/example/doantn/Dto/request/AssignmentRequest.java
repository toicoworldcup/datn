package org.example.doantn.Dto.request;

public class AssignmentRequest {
    private String maLop;
    private String maGv;
    private String hocKi; // Thêm thuộc tính hocKi

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getMaGv() {
        return maGv;
    }

    public void setMaGv(String maGv) {
        this.maGv = maGv;
    }

    public String getHocKi() {
        return hocKi;
    }

    public void setHocKi(String hocKi) {
        this.hocKi = hocKi;
    }

    public AssignmentRequest() {
    }

    public AssignmentRequest(String maLop, String maGv, String hocKi) {
        this.maLop = maLop;
        this.maGv = maGv;
        this.hocKi = hocKi;
    }
}