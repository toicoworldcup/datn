package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.doantn.Entity.Course;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class CourseDTO {
    private Integer id; // <--- THÊM TRƯỜNG ID Ở ĐÂY
    private String maHocPhan;
    private int soTinChi;
    private String tenMonHoc;
    private String khoiLuong;
    private Integer suggestedSemester;
    private String gradeRatio;

    public CourseDTO() {
    }

    // Cập nhật constructor đầy đủ tham số để bao gồm id
    public CourseDTO(Integer id, String maHocPhan, int soTinChi, String tenMonHoc, String khoiLuong, Integer suggestedSemester, String gradeRatio) {
        this.id = id; // <--- Gán id
        this.maHocPhan = maHocPhan;
        this.soTinChi = soTinChi;
        this.tenMonHoc = tenMonHoc;
        this.khoiLuong = khoiLuong;
        this.suggestedSemester = suggestedSemester;
        this.gradeRatio = gradeRatio;
    }

    // Constructor từ Course Entity - BẮT BUỘC phải gán id từ Entity vào DTO
    public CourseDTO(Course course) {
        this.id = course.getId(); // <--- GÁN ID TỪ ENTITY
        this.maHocPhan = course.getMaHocPhan();
        this.soTinChi = course.getTinChi();
        this.tenMonHoc = course.getName();
        this.khoiLuong = course.getKhoiLuong();
        this.suggestedSemester = course.getSuggestedSemester();
        this.gradeRatio = course.getGradeRatio();
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id); // <--- Bao gồm id trong Map nếu cần
        map.put("maHocPhan", this.maHocPhan);
        map.put("tenMonHoc", this.tenMonHoc);
        map.put("soTinChi", this.soTinChi);
        map.put("khoiLuong", this.khoiLuong);
        map.put("suggestedSemester", this.suggestedSemester);
        map.put("gradeRatio", this.gradeRatio);
        return map;
    }
}