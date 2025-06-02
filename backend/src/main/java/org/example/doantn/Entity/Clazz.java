package org.example.doantn.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "clazz")
public class Clazz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_lop")
    private String maLop;

    @Column(name = "lich_thi")
    private LocalDate lichThi;

    @Column(name = "so_luong_sinh_vien")
    private Integer soLuongSinhVien = 0;

    @JsonIgnore
    @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Dangkilop> dangkilops = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Semester semester;

    @ManyToMany
    @JoinTable(
            name = "teacher_clazz",
            joinColumns = @JoinColumn(name = "clazz_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private Set<Teacher> teachers = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Grade> grades = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Attendance> attendances = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Schedule> schedules = new HashSet<>();

    public Clazz() {
    }

    public Clazz(Integer id, String maLop, LocalDate lichThi, Integer soLuongSinhVien, Course course, Semester semester,
                 Set<Dangkilop> dangkilops, Set<Teacher> teachers,
                 Set<Grade> grades, Set<Attendance> attendances, Set<Schedule> schedules) {
        this.id = id;
        this.maLop = maLop;
        this.lichThi = lichThi;
        this.soLuongSinhVien = soLuongSinhVien;
        this.course = course;
        this.semester = semester;
        this.dangkilops = dangkilops != null ? dangkilops : new HashSet<>();
        this.teachers = teachers != null ? teachers : new HashSet<>();
        this.grades = grades != null ? grades : new HashSet<>();
        this.attendances = attendances != null ? attendances : new HashSet<>();
        this.schedules = schedules != null ? schedules : new HashSet<>();
    }

    // Getters and Setters

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public void setLichThi(LocalDate lichThi) {
        this.lichThi = lichThi;
    }

    public void setSoLuongSinhVien(Integer soLuongSinhVien) {
        this.soLuongSinhVien = soLuongSinhVien;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public void setDangkilops(Set<Dangkilop> dangkilops) {
        this.dangkilops = dangkilops;
    }

    public void setTeachers(Set<Teacher> teachers) {
        this.teachers = teachers;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

    public void setAttendances(Set<Attendance> attendances) {
        this.attendances = attendances;
    }

    public void setSchedules(Set<Schedule> schedules) {
        this.schedules = schedules;
    }
}
