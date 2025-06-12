package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.doantn.Entity.Dangkihocphan;
import org.example.doantn.Entity.Course; // Đảm bảo import Course

@Getter
@Setter
public class DkhpDTO {
    private Integer id; // THÊM TRƯỜNG ID
    private String maHocPhan;
    private String semesterName;
    private String mssv;
    private String nameCourse; // Tên học phần (trước đây là tenMonHoc)
    private Double finalGrade; // Điểm học phần cuối cùng
    private String gradeLetter; // Xếp loại (A, B, C, D, F)
    private int tinchi; // Số tín chỉ (trước đây là soTinChi)


    public DkhpDTO(Integer id, String maHocPhan, String mssv, String semesterName, Double finalGrade, String gradeLetter, int tinchi, String nameCourse) {
        this.id = id; // Khởi tạo ID trong constructor
        this.maHocPhan = maHocPhan;
        this.mssv = mssv;
        this.semesterName = semesterName;
        this.finalGrade = finalGrade;
        this.gradeLetter = gradeLetter;
        this.tinchi = tinchi;
        this.nameCourse = nameCourse;
    }

    public DkhpDTO(Dangkihocphan dangkihocphan) {
        this.id = dangkihocphan.getId(); // Gán ID từ entity Dangkihocphan
        this.maHocPhan = dangkihocphan.getCourse().getMaHocPhan();
        this.mssv = dangkihocphan.getStudent().getMssv();
        this.semesterName = dangkihocphan.getSemester().getName();
        this.finalGrade = dangkihocphan.getFinalGrade();
        this.gradeLetter = dangkihocphan.getGradeLetter();
        this.tinchi = dangkihocphan.getCourse().getTinChi(); // Chắc chắn là getTinChi() chứ không phải getSoTinChi() nếu tên khác
        this.nameCourse = dangkihocphan.getCourse().getName(); // Chắc chắn là getName() chứ không phải getTenMonHoc() nếu tên khác
    }
}