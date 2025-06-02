package org.example.doantn.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "dang_ki_hoc_phan")
@Getter
@Setter
@NoArgsConstructor
public class Dangkihocphan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "điểm_gk")
    private Double gki;

    @Column(name = "điểm_ck")
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

    @Column(name = "diem_hoc_phan")
    private Double finalGrade;

    @Column(name = "xep_loai")
    private String gradeLetter;

    public Dangkihocphan(Student student, Course course) {
        this.student = student;
        this.course = course;
    }

    public Dangkihocphan(Course course, Integer id, Semester semester, Student student, Double gki, Double cki, Double finalGrade, String gradeLetter) {
        this.course = course;
        this.id = id;
        this.semester = semester;
        this.student = student;
        this.gki = gki;
        this.cki = cki;
        this.finalGrade = finalGrade;
        this.gradeLetter = gradeLetter;
    }
}