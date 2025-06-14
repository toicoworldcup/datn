package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ClassTeacherDTO {
    private String classIdentifier; // Ví dụ: "MãLớp - TênHọcKỳ"
    private List<String> teacherCodes;

    public ClassTeacherDTO(String classIdentifier, List<String> teacherCodes) {
        this.classIdentifier = classIdentifier;
        this.teacherCodes = teacherCodes;
    }

}