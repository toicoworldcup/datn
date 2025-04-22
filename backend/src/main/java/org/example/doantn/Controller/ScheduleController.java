package org.example.doantn.Controller;
import org.example.doantn.Dto.response.ScheduleDTO;
import org.example.doantn.Entity.Schedule;
import org.example.doantn.Service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedule")
@PreAuthorize("hasRole('ADMIN')")
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }


    @GetMapping("/{malop}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Schedule>> getScheduleByClazz(@PathVariable String maLop) {
        List<Schedule> schedules = scheduleService.getScheduleByMaLop(maLop);
        return ResponseEntity.ok(schedules);
    }
}

