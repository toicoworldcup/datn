package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.doantn.Entity.Dangkihocphan;

@Getter
@Setter
public class DkhpDTO {
    private String maHocPhan;
    private String semesterName;
    private String mssv;
    private Double finalGrade; // Điểm học phần cuối cùng
    private String gradeLetter; // Xếp loại (A, B, C, D, F)


    public DkhpDTO(String maHocPhan, String mssv, String semesterName, Double finalGrade, String gradeLetter) {
        this.maHocPhan = maHocPhan;
        this.mssv = mssv;
        this.semesterName = semesterName;
        this.finalGrade = finalGrade;
        this.gradeLetter = gradeLetter;
    }

    public DkhpDTO(Dangkihocphan dangkihocphan) {
        this.maHocPhan = dangkihocphan.getCourse().getMaHocPhan();
        this.mssv = dangkihocphan.getStudent().getMssv();
        this.semesterName = dangkihocphan.getSemester().getName();
        this.finalGrade = dangkihocphan.getFinalGrade();
        this.gradeLetter = dangkihocphan.getGradeLetter();
    }
}