package org.example.doantn.Dto.response;

import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Teacher;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ClazzDTO {
    private String maLop;
    private String maHocPhan;
    private String hocki;
    private LocalDate lichThi;
    private List<TeacherDTO> teachers; // Thêm thuộc tính danh sách giáo viên

    public ClazzDTO(Clazz clazz) {
        this.maLop = clazz.getMaLop();
        if (clazz.getSemester() != null) {
            this.hocki = clazz.getSemester().getName();
        }
        if (clazz.getCourse() != null) {
            this.maHocPhan = clazz.getCourse().getMaHocPhan();
        }
        this.lichThi = clazz.getLichThi();
        if (clazz.getTeachers() != null) {
            this.teachers = clazz.getTeachers().stream()
                    .map(TeacherDTO::new)
                    .collect(Collectors.toList());
        } else {
            this.teachers = null;
        }
    }

    public ClazzDTO(String maLop, String hocki, String maHocPhan, LocalDate lichThi) {
        this.maLop = maLop;
        this.hocki = hocki;
        this.maHocPhan = maHocPhan;
        this.lichThi = lichThi;
        this.teachers = null; // Khi tạo DTO theo cách này, danh sách giáo viên có thể null hoặc được set sau
    }

    public ClazzDTO() {
        this.teachers = null;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getMaHocPhan() {
        return maHocPhan;
    }

    public void setMaHocPhan(String maHocPhan) {
        this.maHocPhan = maHocPhan;
    }

    public String getHocki() {
        return hocki;
    }

    public void setHocki(String hocki) {
        this.hocki = hocki;
    }

    public LocalDate getLichThi() {
        return lichThi;
    }

    public void setLichThi(LocalDate lichThi) {
        this.lichThi = lichThi;
    }

    public List<TeacherDTO> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherDTO> teachers) {
        this.teachers = teachers;
    }
}