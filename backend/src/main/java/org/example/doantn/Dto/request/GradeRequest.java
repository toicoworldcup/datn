package org.example.doantn.Dto.request;

import jakarta.validation.constraints.*;

public class GradeRequest {

    @NotNull(message = "Mã sinh viên không được để trống")
    private String mssv;

    @NotNull(message = "Mã lớp không được để trống")
    private String maLop;

    @NotNull(message = "Mã học kỳ không được để trống")
    private String semesterName;

    @NotNull(message = "Điểm không được để trống")
    @DecimalMin(value = "0.0", message = "Điểm phải >= 0")
    @DecimalMax(value = "10.0", message = "Điểm phải <= 10")
    private Double gki;
    @DecimalMin(value = "0.0", message = "Điểm phải >= 0")
    @DecimalMax(value = "10.0", message = "Điểm phải <= 10")
    private Double cki;

    public @DecimalMin(value = "0.0", message = "Điểm phải >= 0") @DecimalMax(value = "10.0", message = "Điểm phải <= 10") Double getCki() {
        return cki;
    }

    public void setCki(@DecimalMin(value = "0.0", message = "Điểm phải >= 0") @DecimalMax(value = "10.0", message = "Điểm phải <= 10") Double cki) {
        this.cki = cki;
    }


    public @NotNull(message = "Điểm không được để trống") @DecimalMin(value = "0.0", message = "Điểm phải >= 0") @DecimalMax(value = "10.0", message = "Điểm phải <= 10") Double getGki() {
        return gki;
    }

    public void setGki(@NotNull(message = "Điểm không được để trống") @DecimalMin(value = "0.0", message = "Điểm phải >= 0") @DecimalMax(value = "10.0", message = "Điểm phải <= 10") Double gki) {
        this.gki = gki;
    }

    public @NotNull(message = "Mã lớp không được để trống") String getMaLop() {
        return maLop;
    }

    public void setMaLop(@NotNull(message = "Mã lớp không được để trống") String maLop) {
        this.maLop = maLop;
    }

    public @NotNull(message = "Mã sinh viên không được để trống") String getMssv() {
        return mssv;
    }

    public void setMssv(@NotNull(message = "Mã sinh viên không được để trống") String mssv) {
        this.mssv = mssv;
    }

    public @NotNull(message = "Mã học kỳ không được để trống") String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(@NotNull(message = "Mã học kỳ không được để trống") String semesterName) {
        this.semesterName = semesterName;
    }
}

