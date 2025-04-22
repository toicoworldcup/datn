package org.example.doantn.Dto.response;

import org.example.doantn.Entity.Schedule;

public class ScheduleDTO {
    private String clazzName;
    private String roomName;
    private String timeSlot;
    private String dayOfWeek;
    private String semesterName;

    public ScheduleDTO(Schedule schedule) {
        this.clazzName = (schedule.getClazz() != null) ? schedule.getClazz().getMaLop() : "N/A"; // Kiểm tra null
        this.roomName = (schedule.getRoom() != null) ? schedule.getRoom().getName() : "N/A";
        this.timeSlot = (schedule.getTimeSlot() != null) ? schedule.getTimeSlot().getName() : "N/A";
        this.dayOfWeek = schedule.getDayOfWeek();
        this.semesterName = (schedule.getSemester() != null) ? schedule.getSemester().getName() : "N/A";
    }

    public ScheduleDTO(String clazzName, String roomName, String timeSlot,String dayOfWeek, String semesterName) {
        this.clazzName = clazzName;
        this.dayOfWeek = dayOfWeek;
        this.roomName = roomName;
        this.semesterName = semesterName;
        this.timeSlot = timeSlot;
    }

    public ScheduleDTO() {}

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }
}
