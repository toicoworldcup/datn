package org.example.doantn.Dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScheduleDTO {
    private String clazzName;
    private String roomName;
    private String timeSlot;
    private String dayOfWeek;
    private String semesterName;
    private String nameCourse;


    public ScheduleDTO(String clazzName, String roomName, String timeSlot,String dayOfWeek, String semesterName, String nameCourse ) {
        this.clazzName = clazzName;
        this.dayOfWeek = dayOfWeek;
        this.roomName = roomName;
        this.semesterName = semesterName;
        this.timeSlot = timeSlot;
        this.nameCourse = nameCourse;
    }


}
