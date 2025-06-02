package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.doantn.Entity.Course;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class CourseDTO {
    private String maHocPhan;
    private int soTinChi;
    private String tenMonHoc;
    private String khoiLuong;
    private Integer suggestedSemester;
    private String gradeRatio; // Thêm trường này


    public CourseDTO(String maHocPhan, int soTinChi, String tenMonHoc, String khoiLuong, Integer suggestedSemester, String gradeRatio) {
        this.maHocPhan = maHocPhan;
        this.soTinChi = soTinChi;
        this.tenMonHoc = tenMonHoc;
        this.khoiLuong = khoiLuong;
        this.suggestedSemester = suggestedSemester;
        this.gradeRatio = gradeRatio;
    }

    public CourseDTO(Course course) {
        this.maHocPhan = course.getMaHocPhan();
        this.soTinChi = course.getTinChi();
        this.tenMonHoc = course.getName();
        this.khoiLuong = course.getKhoiLuong();
        this.suggestedSemester = course.getSuggestedSemester();
        this.gradeRatio = course.getGradeRatio(); // Lấy giá trị từ entity
    }
    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("maHocPhan", this.maHocPhan);
        map.put("tenMonHoc", this.tenMonHoc);
        map.put("soTinChi", this.soTinChi);
        map.put("khoiLuong", this.khoiLuong);
        map.put("suggestedSemester", this.suggestedSemester);
        map.put("gradeRatio", this.gradeRatio);
        return map;
    }
}