package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.doantn.Entity.Clazz;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class ClazzDTO {
    private String maLop;
    private String maHocPhan;
    private String hocki;
    private LocalDate lichThi;
    private List<TeacherDTO> teachers;
    private Integer soLuongSinhVien; // Thêm thuộc tính số lượng sinh viên

    public ClazzDTO(Clazz clazz) {
        this.maLop = clazz.getMaLop();
        if (clazz.getSemester() != null) {
            this.hocki = clazz.getSemester().getName();
        }
        if (clazz.getCourse() != null) {
            this.maHocPhan = clazz.getCourse().getMaHocPhan();
        }
        this.lichThi = clazz.getLichThi();
        this.soLuongSinhVien = clazz.getSoLuongSinhVien(); // Lấy số lượng sinh viên từ entity
        if (clazz.getTeachers() != null) {
            this.teachers = clazz.getTeachers().stream()
                    .map(TeacherDTO::new)
                    .collect(Collectors.toList());
        } else {
            this.teachers = null;
        }
    }

}