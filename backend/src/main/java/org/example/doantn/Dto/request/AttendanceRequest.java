package org.example.doantn.Dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.example.doantn.Entity.AStatus;
import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Semester;
import org.example.doantn.Entity.Student;

import java.time.LocalDate;

@Data
public class AttendanceRequest {
    private AStatus status;
    private LocalDate attendanceDate;
    private String maLop;
    private String mssv;
    private String hocKi;

}
