package org.example.doantn.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="course")
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "ma_hoc_phan")
    private String maHocPhan;

    @Column(name = "khoi_luong")
    private String khoiLuong;

    @Column(name = "tin_chi")
    private int tinChi;

    @Column(name = "hoc_ky_goi_y")
    private Integer suggestedSemester;

    @Column(name = "ti_le_diem") // Thêm cột tỉ lệ điểm
    private String gradeRatio;

    @ManyToMany(mappedBy = "courses")
    @JsonIgnore
    private Set<Ctdt> ctdts = new HashSet<>();

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private Set<Dangkihocphan> dangkihocphans;

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private Set<Clazz> clazzes;

    public Course() {
    }

    public Course(Set<Clazz> clazzes, Set<Dangkihocphan> dangkihocphans, Integer id, String maHocPhan, String name, int tinChi, String khoiLuong, Integer suggestedSemester, String gradeRatio) {
        this.clazzes = clazzes;
        this.dangkihocphans = dangkihocphans;
        this.id = id;
        this.maHocPhan = maHocPhan;
        this.name = name;
        this.tinChi = tinChi;
        this.khoiLuong = khoiLuong;
        this.suggestedSemester = suggestedSemester;
        this.gradeRatio = gradeRatio;
    }

    // Getters and Setters đã được tự động tạo bởi Lombok (@Getter, @Setter)

    // Các phương thức khác (nếu có)
}