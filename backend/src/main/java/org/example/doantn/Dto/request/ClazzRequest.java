package org.example.doantn.Dto.request;


import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class ClazzRequest {
    private String maLop;
    private LocalDate lichThi;
    private String maHocPhan;
    private String hocki;
}

