package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;
import org.example.doantn.Entity.Ctdt;
import org.example.doantn.Entity.Batch;

@Setter
@Getter
public class CtdtDTO {
    private String name;
    private String maCt;
    private String khoa;

    // Constructor nhận entity Ctdt (nên dùng để chuyển đổi đầy đủ)
    public CtdtDTO(Ctdt ctdt) {
        this.name = ctdt.getName();
        this.maCt = ctdt.getMaCt();
        Batch batch = ctdt.getBatch();
        this.khoa = (batch != null) ? batch.getName() : null; // Kiểm tra Batch null
    }

}