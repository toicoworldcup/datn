package org.example.doantn.Dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Data
@NoArgsConstructor
public class CthDTO {
    private String name;       // Tên chương trình đào tạo (có vẻ đây là tên CTĐT, bạn có muốn hiển thị tên môn học không?)
    private String maHocPhan;  // Mã học phần
    private String tenMonHoc;  // Tên môn học (thêm thuộc tính này)
    private int tinChi;        // Số tín chỉ
    private Double diemGK;       // Điểm giữa kỳ
    private Double diemCK;       // Điểm cuối kỳ

    public CthDTO(String name, String maHocPhan, String tenMonHoc, int tinChi, Double diemGK, Double diemCK) {
        this.name = name;
        this.maHocPhan = maHocPhan;
        this.tenMonHoc = tenMonHoc;
        this.tinChi = tinChi;
        this.diemGK = diemGK;
        this.diemCK = diemCK;
    }

    // Getters và Setters (đã được Lombok tạo tự động với @Data)
}