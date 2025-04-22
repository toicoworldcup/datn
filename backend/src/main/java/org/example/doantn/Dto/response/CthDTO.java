package org.example.doantn.Dto.response;

public class CthDTO {
    private String name;       // Tên chương trình đào tạo
    private String maHocPhan;  // Mã học phần
    private int tinChi;        // Số tín chỉ

    public CthDTO(String name, String maHocPhan, int tinChi) {
        this.name = name;
        this.maHocPhan = maHocPhan;
        this.tinChi = tinChi;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getMaHocPhan() {
        return maHocPhan;
    }

    public int getTinChi() {
        return tinChi;
    }
}
