package org.example.doantn.Controller;

import org.example.doantn.Dto.response.ScheduleDTO;
import org.example.doantn.Entity.Schedule;
import org.example.doantn.Service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/generate/{semesterName}")
    public ResponseEntity<List<ScheduleDTO>> generateSchedule(@PathVariable String semesterName) {
        try {
            List<Schedule> schedules = scheduleService.generateSchedule(semesterName);
            List<ScheduleDTO> scheduleDTOs = schedules.stream().map(schedule -> new ScheduleDTO(
                    schedule.getClazz().getMaLop(),
                    schedule.getRoom().getName(),
                    schedule.getTimeSlot().getName(),
                    schedule.getDayOfWeek(),
                    schedule.getSemester().getName()
            )).collect(Collectors.toList());
            return ResponseEntity.ok(scheduleDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @GetMapping("/semester/{semesterName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScheduleDTO>> getScheduleBySemester(@PathVariable String semesterName) {
        List<Schedule> schedules = scheduleService.getScheduleBySemesterName(semesterName);
        List<ScheduleDTO> scheduleDTOs = schedules.stream().map(schedule -> new ScheduleDTO(
                schedule.getClazz().getMaLop(),
                schedule.getRoom().getName(),
                schedule.getTimeSlot().getName(),
                schedule.getDayOfWeek(),
                schedule.getSemester().getName()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(scheduleDTOs);
    }

    @GetMapping("/{malop}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Schedule>> getScheduleByClazz(@PathVariable String malop) {
        List<Schedule> schedules = scheduleService.getScheduleByMaLop(malop);
        return ResponseEntity.ok(schedules);
    }
    @GetMapping("/class/{maLop}/semester/{semesterName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScheduleDTO>> getScheduleByClazzAndSemester(
            @PathVariable String maLop,
            @PathVariable String semesterName) {
        List<Schedule> schedules = scheduleService.getScheduleByClazzAndSemester(maLop, semesterName);
        List<ScheduleDTO> scheduleDTOs = schedules.stream().map(schedule -> new ScheduleDTO(
                schedule.getClazz().getMaLop(),
                schedule.getRoom().getName(),
                schedule.getTimeSlot().getName(),
                schedule.getDayOfWeek(),
                schedule.getSemester().getName()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(scheduleDTOs);
    }

}