package org.example.doantn.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    @OneToMany(mappedBy = "clazz")
    private Set<Dangkilop> dangkilops;
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

    @OneToMany(mappedBy = "clazz")
    private Set<Grade> grades;
    @OneToMany(mappedBy = "clazz")
    private Set<Attendance> attendances;
    @OneToMany(mappedBy = "clazz")
    private Set<Schedule> schedules;

    public Clazz(Set<Attendance> attendances, Course course, Set<Dangkilop> dangkilops, Set<Grade> grades, Integer id, LocalDate lichThi, String maLop, Set<Schedule> schedules, Semester semester, Set<Teacher> teachers) {
        this.attendances = attendances;
        this.course = course;
        this.dangkilops = dangkilops;
        this.grades = grades;
        this.id = id;
        this.lichThi = lichThi;
        this.maLop = maLop;
        this.schedules = schedules;
        this.semester = semester;
        this.teachers = teachers;
    }

    public Clazz() {
    }

    public Set<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(Set<Attendance> attendances) {
        this.attendances = attendances;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Set<Dangkilop> getDangkilops() {
        return dangkilops;
    }

    public void setDangkilops(Set<Dangkilop> dangkilops) {
        this.dangkilops = dangkilops;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getLichThi() {
        return lichThi;
    }

    public void setLichThi(LocalDate lichThi) {
        this.lichThi = lichThi;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public Set<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<Schedule> schedules) {
        this.schedules = schedules;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Set<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(Set<Teacher> teachers) {
        this.teachers = teachers;
    }
}