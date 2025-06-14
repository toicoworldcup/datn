package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentGradeDTO {
    private String maLop;
    private String semesterName;
    private String name;
    private String mssv;
    private Double diemGk;
    private Double diemCk;
    private String history; // Thêm trường history

    public StudentGradeDTO() {
    }

    public StudentGradeDTO(String maLop, String semesterName, String name, String mssv, Double diemGk, Double diemCk, String history) {
        this.maLop = maLop;
        this.semesterName = semesterName;
        this.name = name;
        this.mssv = mssv;
        this.diemGk = diemGk;
        this.diemCk = diemCk;
        this.history = history;
    }
}