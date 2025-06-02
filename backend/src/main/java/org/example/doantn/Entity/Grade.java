package org.example.doantn.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Setter
@Getter
@Entity
@Table(name = "grade")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "clazz_id")
    private Clazz clazz;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @Column(name = "diem_gk")
    private Double diemGk;

    @Column(name = "diem_ck")
    private Double diemCk;

    @Column(columnDefinition = "TEXT")
    private String history;

    public Grade() {
        this.history = ""; // Khởi tạo history rỗng
    }

    // Getters and setters

    public void appendHistory(Double oldGk, Double newGk, Double oldCk, Double newCk) {
        StringBuilder sb = new StringBuilder(this.history != null ? this.history : "");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        sb.append("[").append(now.format(formatter)).append("] ");
        boolean hasChange = false;
        if ((oldGk == null && newGk != null) || (oldGk != null && !oldGk.equals(newGk))) {
            sb.append("GK: ").append(oldGk != null ? oldGk : "N/A").append(" -> ").append(newGk != null ? newGk : "N/A").append("; ");
            hasChange = true;
        }
        if ((oldCk == null && newCk != null) || (oldCk != null && !oldCk.equals(newCk))) {
            sb.append("CK: ").append(oldCk != null ? oldCk : "N/A").append(" -> ").append(newCk != null ? newCk : "N/A").append("; ");
            hasChange = true;
        }
        if (hasChange) {
            sb.append("\n");
            this.history = sb.toString();
        }
    }
}