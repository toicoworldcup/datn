package org.example.doantn.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "diem_danh")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private AStatus status;
    @Column(name = "date")
    private LocalDate attendanceDate;
    @ManyToOne
    @JoinColumn(name = "clazz_id")
    private Clazz clazz;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;


    public Attendance() {
    }

    public Attendance(LocalDate attendanceDate, Clazz clazz, Semester semester, AStatus status, Student student) {
        this.attendanceDate = attendanceDate;
        this.clazz = clazz;
        this.semester = semester;
        this.status = status;
        this.student = student;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public Clazz getClazz() {
        return clazz;
    }

    public void setClazz(Clazz clazz) {
        this.clazz = clazz;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AStatus getStatus() {
        return status;
    }

    public void setStatus(AStatus status) {
        this.status = status;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
