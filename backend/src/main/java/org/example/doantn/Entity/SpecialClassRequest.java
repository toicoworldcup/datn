package org.example.doantn.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "special_class_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpecialClassRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_mssv", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "clazz_id", nullable = false)
    private Clazz clazz;

    @ManyToOne
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(nullable = false)
    private String requestDate; // Ngày gửi yêu cầu

    @Column(nullable = false)
    private String status; // Trạng thái: "PENDING", "APPROVED", "REJECTED"

    // Có thể thêm các trường khác nếu cần, ví dụ: lý do yêu cầu

    @Override
    public String toString() {
        return "SpecialClassRequest{" +
                "id=" + id +
                ", student=" + (student != null ? student.getMssv() : null) +
                ", clazz=" + (clazz != null ? clazz.getMaLop() : null) +
                ", semester=" + (semester != null ? semester.getName() : null) +
                ", requestDate='" + requestDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}