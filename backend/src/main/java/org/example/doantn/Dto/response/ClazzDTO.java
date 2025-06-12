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
    private Integer id; // THÊM TRƯỜNG ID Ở ĐÂY
    private String maLop;
    private String maHocPhan;
    private String hocki;
    private LocalDate lichThi;
    private List<TeacherDTO> teachers;
    private Integer soLuongSinhVien;

    public ClazzDTO(Clazz clazz) {
        this.id = clazz.getId(); // GÁN ID TỪ ENTITY VÀO DTO
        this.maLop = clazz.getMaLop();
        if (clazz.getSemester() != null) {
            this.hocki = clazz.getSemester().getName();
        }
        if (clazz.getCourse() != null) {
            this.maHocPhan = clazz.getCourse().getMaHocPhan();
        }
        this.lichThi = clazz.getLichThi();
        this.soLuongSinhVien = clazz.getSoLuongSinhVien();
        if (clazz.getTeachers() != null) {
            this.teachers = clazz.getTeachers().stream()
                    .map(TeacherDTO::new)
                    .collect(Collectors.toList());
        } else {
            this.teachers = null;
        }
    }
}