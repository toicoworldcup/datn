package org.example.doantn.Dto.response;

import org.example.doantn.Entity.Ctdt;

public class CtdtDTO {
    private String name;
    private String maCt;
    private String khoa;
    public CtdtDTO(String name) {
        this.name = name;
    }
    public CtdtDTO(Ctdt ctdt) {
        this.name = ctdt.getName();
        this.maCt=ctdt.getMaCt();
        this.khoa=ctdt.getBatch().getName();
    }

    public String getMaCt() {
        return maCt;
    }

    public void setMaCt(String maCt) {
        this.maCt = maCt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKhoa() {
        return khoa;
    }

    public void setKhoa(String khoa) {
        this.khoa = khoa;
    }
}
