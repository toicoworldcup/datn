package org.example.doantn.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="dang_ki_hoc_phan")
public class Dangkihocphan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="điểm_gk")
    private Double gki;
    @Column(name="điểm_ck")
    private Double cki;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    @ManyToOne
    @JoinColumn(name = "semester_id")
    @OnDelete(action = OnDeleteAction.CASCADE)

    private Semester semester;

    public Dangkihocphan(Course course, Integer id, Semester semester, Student student) {
        this.course = course;
        this.id = id;
        this.semester = semester;
        this.student = student;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Double getCki() {
        return cki;
    }

    public void setCki(Double cki) {
        this.cki = cki;
    }

    public Double getGki() {
        return gki;
    }

    public void setGki(Double gki) {
        this.gki = gki;
    }

    public Dangkihocphan() {
    }

    public Dangkihocphan(Student student, Course course) {
        this.student = student;
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
