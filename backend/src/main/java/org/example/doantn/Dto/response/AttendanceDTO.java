package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class AttendanceDTO {
    private LocalDate attendanceDate;
    private String hocKi;
    private String maLop;
    private String mssv;
    private String status;
    private String studentName; // Thêm trường này




    public AttendanceDTO(LocalDate attendanceDate, String hocKi, String maLop, String mssv, String status, String studentName) {
        this.attendanceDate = attendanceDate;
        this.hocKi = hocKi;
        this.maLop = maLop;
        this.mssv = mssv;
        this.status = status;
        this.studentName = studentName;
    }

    // Getters and setters...

}