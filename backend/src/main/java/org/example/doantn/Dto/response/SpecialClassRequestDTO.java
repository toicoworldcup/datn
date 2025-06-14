package org.example.doantn.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.doantn.Entity.SpecialClassRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialClassRequestDTO {
    private Integer id;
    private String requestDate;
    private String status;
    private String studentMssv;
    private String studentName;
    private String clazzMaLop;
    private String semesterName;

    public SpecialClassRequestDTO(SpecialClassRequest request) {
        this.id = request.getId();
        this.requestDate = request.getRequestDate();
        this.status = request.getStatus();
        if (request.getStudent() != null) {
            this.studentMssv = request.getStudent().getMssv();
            this.studentName = request.getStudent().getName();
        }
        if (request.getClazz() != null) {
            this.clazzMaLop = request.getClazz().getMaLop();
        }
        if (request.getSemester() != null) {
            this.semesterName = request.getSemester().getName();
        }
    }
}