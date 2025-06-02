package org.example.doantn.Dto.request;

import lombok.Data;

@Data
public class CourseRequest {
    private String maHocPhan;
    private int soTinChi;
    private String tenMonHoc;
    private String khoiLuong;
    private Integer suggestedSemester;
    private String gradeRatio; // Thêm trường này
}