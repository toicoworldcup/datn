package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserInfoResponse {
    // Getter và Setter cho fullName và role
    private String fullName;
    private String role;

    public UserInfoResponse(String fullName, String role) {
        this.fullName = fullName;
        this.role = role;
    }

}