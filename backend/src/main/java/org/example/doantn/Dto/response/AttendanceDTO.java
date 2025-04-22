package org.example.doantn.Dto.response;

import org.example.doantn.Entity.AStatus;
import org.example.doantn.Entity.Attendance;

import java.time.LocalDate;

public class AttendanceDTO {
    private AStatus status;
    private LocalDate attendanceDate;
    private String maLop;
    private String mssv;
    private String hocKi;

    public AttendanceDTO(LocalDate attendanceDate, String hocKi, String maLop, String mssv, AStatus status) {
        this.attendanceDate = attendanceDate;
        this.hocKi = hocKi;
        this.maLop = maLop;
        this.mssv = mssv;
        this.status = status;
    }
    public AttendanceDTO(Attendance attendance) {
        this.attendanceDate = attendance.getAttendanceDate();
        this.hocKi=attendance.getSemester().getName();
        this.maLop=attendance.getClazz().getMaLop();
        this.mssv=attendance.getStudent().getMssv();
        this.status=attendance.getStatus();

    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getHocKi() {
        return hocKi;
    }

    public void setHocKi(String hocKi) {
        this.hocKi = hocKi;
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

    public AStatus getStatus() {
        return status;
    }

    public void setStatus(AStatus status) {
        this.status = status;
    }
}
