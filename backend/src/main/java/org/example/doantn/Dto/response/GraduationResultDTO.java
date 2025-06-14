package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;

public class GraduationResultDTO {
    private boolean isEligible;
    @Setter
    @Getter
    private String message;

    public GraduationResultDTO(boolean isEligible, String message) {
        this.isEligible = isEligible;
        this.message = message;
    }

    public boolean isEligible() {
        return isEligible;
    }

    public void setEligible(boolean eligible) {
        isEligible = eligible;
    }

}