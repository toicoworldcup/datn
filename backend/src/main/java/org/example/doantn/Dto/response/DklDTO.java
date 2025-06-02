package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.doantn.Entity.Dangkilop;

@Setter
@Getter
public class DklDTO {
    private Integer id; // Thêm trường ID
    private String maLop;
    private String semesterName;
    private String mssv;

    public DklDTO(Integer id, String maLop, String semesterName, String mssv) {
        this.id = id;
        this.maLop = maLop;
        this.semesterName = semesterName;
        this.mssv = mssv;
    }

    public DklDTO(Dangkilop dangkilop) {
        this.id = dangkilop.getId(); // Lấy ID từ entity
        this.maLop = dangkilop.getClazz().getMaLop();
        this.semesterName = dangkilop.getSemester().getName();
        this.mssv = dangkilop.getStudent().getMssv();
    }

}