package org.example.doantn.Service;

import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Room;
import org.example.doantn.Entity.Schedule;
import org.example.doantn.Entity.TimeSlot;
import org.example.doantn.Entity.Semester;
import org.example.doantn.Repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepo scheduleRepo;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private TimeslotRepo timeslotRepo;

    @Autowired
    private ClazzRepo clazzRepo;

    @Autowired
    private SemesterRepo semesterRepo;
    public List<Schedule> getScheduleByMaLop(String maLop) {
        return scheduleRepo.findByClazz_MaLop(maLop);
    }
    public List<Schedule> generateSchedule(String semesterName) {
        scheduleRepo.deleteBySemesterName(semesterName);
        Semester semester = semesterRepo.findByNameAndIsOpen(semesterName, true)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ '" + semesterName + "' hoặc học kỳ này chưa mở."));
        List<Clazz> classes = clazzRepo.findBySemester(semester);
        List<Room> rooms = roomRepo.findAll();
        List<TimeSlot> timeslots = timeslotRepo.findAll();
        if (classes.isEmpty()) {
            throw new RuntimeException("Không có lớp nào thuộc học kỳ '" + semesterName + "'.");
        }
        List<Schedule> newSchedules = new ArrayList<>();
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int dayIndex = 0;
        int roomIndex = 0;
        for (Clazz clazz : classes) {
            for (TimeSlot timeslot : timeslots) {
                Room room = rooms.get(roomIndex % rooms.size());
                while (scheduleRepo.existsByRoomAndTimeSlotAndDayOfWeekAndSemester(room, timeslot, daysOfWeek[dayIndex], semester)) {
                    roomIndex++;
                    room = rooms.get(roomIndex % rooms.size());
                }
             Schedule schedule = new Schedule(clazz, daysOfWeek[dayIndex], room, semester, timeslot);
                newSchedules.add(schedule);
             dayIndex = (dayIndex + 1) % 7;
            }
        }
     return scheduleRepo.saveAll(newSchedules);
    }

}
