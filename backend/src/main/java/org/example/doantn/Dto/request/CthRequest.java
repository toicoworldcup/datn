package org.example.doantn.Dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CthRequest {
    private String maCt;
    private List<String> maHocPhanList;  // Danh sách mã học phần
}
