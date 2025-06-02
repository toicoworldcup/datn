package org.example.doantn.Service;

import org.example.doantn.Entity.*;
import org.example.doantn.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    private final Random random = new Random();
    private final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public List<Schedule> getScheduleByMaLop(String maLop) {
        return scheduleRepo.findByClazz_MaLop(maLop);
    }

    public List<Schedule> getScheduleBySemesterName(String semesterName) {
        Semester semester = semesterRepo.findByName(semesterName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với tên: " + semesterName));
        return scheduleRepo.findBySemester(semester);
    }

    public List<Schedule> getScheduleByClazzAndSemester(String maLop, String semesterName) {
        Semester semester = semesterRepo.findByName(semesterName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với tên: " + semesterName));
        return scheduleRepo.findByClazz_MaLopAndSemester_Name(maLop, semesterName);
    }

    public List<Schedule> getScheduleByTeacherAndSemester(String maGv, String semesterName) {
        return scheduleRepo.findSchedulesByTeacherMaGvAndSemesterName(maGv, semesterName);
    }
    public List<Schedule> getScheduleByStudentAndSemester(String mssv, String semesterName) {

        return scheduleRepo.findSchedulesByStudentMssvAndSemesterName(mssv, semesterName);
    }

    public List<Schedule> generateSchedule(String semesterName) {
        Semester semester = semesterRepo.findByNameAndIsOpen(semesterName, true)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ '" + semesterName + "' hoặc học kỳ này chưa mở."));

        // Kiểm tra xem lịch trình đã tồn tại cho học kỳ này chưa
        if (scheduleRepo.countSchedulesBySemesterId(semester.getId()) > 0) {
            throw new RuntimeException("Thời khóa biểu đã được tạo cho học kỳ '" + semesterName + "' rồi.");
        }

        // Phần còn lại của phương thức giữ nguyên
        List<Clazz> classes = clazzRepo.findBySemester(semester);
        List<Room> rooms = roomRepo.findAll();
        List<TimeSlot> timeslots = timeslotRepo.findAll();

        if (classes.isEmpty()) {
            throw new RuntimeException("Không có lớp nào thuộc học kỳ '" + semesterName + "'.");
        }

        List<Schedule> newSchedules = new ArrayList<>();

        for (Clazz clazz : classes) {
            Course course = clazz.getCourse();
            if (course == null || course.getKhoiLuong() == null) {
                System.out.println("Cảnh báo: Lớp " + clazz.getMaLop() + " không có thông tin khối lượng học phần. Bỏ qua.");
                continue;
            }

            int totalTimeslotsNeeded = calculateTotalTimeslots(course.getKhoiLuong());
            if (totalTimeslotsNeeded == 0) continue;

            // Chọn một phòng cho lớp này
            Room selectedRoom = null;
            int roomAttempts = 0;
            int maxRoomAttempts = rooms.size();
            while (selectedRoom == null && roomAttempts < maxRoomAttempts) {
                Room room = rooms.get(random.nextInt(rooms.size()));
                if (!isRoomOccupiedForClass(newSchedules, room, semester)) {
                    selectedRoom = room;
                }
                roomAttempts++;
            }
            if (selectedRoom == null) {
                System.out.println("Không tìm được phòng trống cho lớp " + clazz.getMaLop() + " trong học kỳ " + semesterName + ".");
                continue; // Chuyển sang lớp tiếp theo nếu không tìm được phòng
            }

            for (int i = 0; i < totalTimeslotsNeeded; i++) {
                boolean assigned = false;
                int maxAttempts = 100;
                int attempts = 0;

                while (!assigned && attempts < maxAttempts) {
                    String day = daysOfWeek[random.nextInt(daysOfWeek.length)];
                    TimeSlot timeslot = timeslots.get(random.nextInt(timeslots.size()));

                    if (!scheduleRepo.existsByRoomAndTimeSlotAndDayOfWeekAndSemester(selectedRoom, timeslot, day, semester)) {
                        Schedule schedule = new Schedule(clazz, day, selectedRoom, semester, timeslot);
                        newSchedules.add(schedule);
                        assigned = true;
                    }
                    attempts++;
                }
                if (!assigned) {
                    System.out.println("Không thể xếp lịch cho tiết " + (i + 1) + " của lớp " + clazz.getMaLop() + " trong học kỳ " + semesterName + ".");
                }
            }
        }
        return scheduleRepo.saveAll(newSchedules);
    }

    private int calculateTotalTimeslots(String khoiLuongStr) {
        String[] parts = khoiLuongStr.split("-");
        if (parts.length >= 2) {
            try {
                return Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("Lỗi định dạng khối lượng học phần: " + khoiLuongStr);
            }
        } else {
            System.out.println("Lỗi định dạng khối lượng học phần: " + khoiLuongStr);
        }
        return 0;
    }

    private boolean isRoomOccupiedForClass(List<Schedule> schedules, Room room, Semester semester) {
        for (Schedule schedule : schedules) {
            if (schedule.getRoom().getId().equals(room.getId()) && schedule.getSemester().getId().equals(semester.getId())) {
                return true;
            }
        }
        return false;
    }

  }
