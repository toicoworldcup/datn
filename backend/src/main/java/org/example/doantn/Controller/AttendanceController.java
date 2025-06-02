package org.example.doantn.Controller;

import org.example.doantn.Dto.request.AttendanceRequest;
import org.example.doantn.Dto.response.AttendanceDTO;
import org.example.doantn.Entity.Attendance;
import org.example.doantn.Repository.ClazzRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Repository.StudentRepo;
import org.example.doantn.Service.AttendanceService;
import org.example.doantn.Service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private SemesterRepo semesterRepo;
    @Autowired
    private ClazzRepo clazzRepo;

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping
    public ResponseEntity<List<AttendanceDTO>> getAllAttendances() {
        List<AttendanceDTO> responses = attendanceService.getAllAttendances().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/check-exists/{maLop}/{hocKi}/{attendanceDate}")
    public ResponseEntity<Boolean> checkAttendanceExists(
            @PathVariable String maLop,
            @PathVariable String hocKi,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate attendanceDate) {
        boolean exists = attendanceService.checkAttendanceExists(maLop, hocKi, attendanceDate);
        return ResponseEntity.ok(exists);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/absent-count/{mssv}/clazz/{maLop}/to-date/{toDate}")
    public ResponseEntity<Integer> getAbsentCount(
            @PathVariable String mssv,
            @PathVariable String maLop,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
           ) {
        int absentCount = attendanceService.getAbsentCount(maLop, mssv, toDate);
        return ResponseEntity.ok(absentCount);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/clazz/{maLop}/semester/{hocki}/date/{date}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByClazzAndSemesterAndDate(
            @PathVariable String maLop,
            @PathVariable String hocki,
            @PathVariable LocalDate date,
            Authentication authentication) {
        if (!hasAccess(authentication, maLop, hocki)) {
            return ResponseEntity.status(403).build();
        }
        List<AttendanceDTO> responses = attendanceService.getAttendanceByClazzAndSemesterAndDate(maLop, hocki, date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/clazz/{maLop}/student/{mssv}/semester/{hocki}/{attendanceDate}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByClazzAndStudentAndSemesterAndDate(
            @PathVariable String maLop,
            @PathVariable String mssv,
            @PathVariable String hocki,
            @PathVariable String attendanceDate,
            Authentication authentication) {
        if (!hasAccess(authentication, maLop, hocki)) {
            return ResponseEntity.status(403).build();
        }
        Attendance attendance = attendanceService.getAttendanceByClazzAndStudentAndSemester(maLop, mssv, hocki, attendanceDate);
        if (attendance != null) {
            List<AttendanceDTO> response = new ArrayList<>();
            response.add(convertToDTO(attendance));
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(new ArrayList<>()); // Trả về mảng rỗng nếu không tìm thấy
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping("/bulk")
    public ResponseEntity<?> addBulkAttendance(
            @RequestBody List<AttendanceRequest> requests,
            Authentication authentication) {
        try {
            List<Attendance> attendancesToSave = new ArrayList<>();
            for (AttendanceRequest request : requests) {
                boolean authorized = hasAccess(authentication, request.getMaLop(), request.getHocKi());
                if (!authorized) {
                    return ResponseEntity.status(403).build();
                }
                Attendance attendance = convertToEntity(request);
                // Kiểm tra trùng lặp trước khi thêm vào danh sách
                LocalDate attendanceDate = request.getAttendanceDate();
                String maLop = request.getMaLop();
                String hocKi = request.getHocKi();
                String mssv = request.getMssv();

                if (attendanceService.getAttendanceByClazzAndStudentAndSemester(maLop, mssv, hocKi, attendanceDate.toString()) == null) {
                    attendancesToSave.add(attendance);
                }
            }
            List<Attendance> savedAttendances = attendanceService.markBulkAttendance(attendancesToSave);
            return ResponseEntity.ok("Đã lưu " + savedAttendances.size() + " điểm danh.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping
    public ResponseEntity<?> addAttendance(
            @RequestBody AttendanceRequest request,
            Authentication authentication) {
        try {
            boolean authorized = hasAccess(authentication, request.getMaLop(), request.getHocKi());
            if (!authorized) {
                return ResponseEntity.status(403).build();
            }
            LocalDate attendanceDate = request.getAttendanceDate();
            String maLop = request.getMaLop();
            String hocKi = request.getHocKi();
            String mssv = request.getMssv();

            if (attendanceService.getAttendanceByClazzAndStudentAndSemester(maLop, mssv, hocKi, attendanceDate.toString()) != null) {
                return ResponseEntity.badRequest().body("Điểm danh cho sinh viên " + mssv + " lớp " + maLop + " ngày " + attendanceDate + " đã tồn tại.");
            }

            Attendance attendance = convertToEntity(request);
            Attendance savedAttendance = attendanceService.markAttendance(attendance);
            return ResponseEntity.ok(convertToDTO(savedAttendance));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/clazz/{maLop}/semester/{hocki}/dates")
    public ResponseEntity<List<String>> getAttendanceDatesByClazzAndSemester(
            @PathVariable String maLop,
            @PathVariable String hocki,
            Authentication authentication) {
        if (!hasAccess(authentication, maLop, hocki)) {
            return ResponseEntity.status(403).build();
        }
        List<String> dates = attendanceService.getAttendanceDatesByClazzAndSemester(maLop, hocki);
        return ResponseEntity.ok(dates);
    }

    private boolean hasAccess(Authentication authentication, String maLop, String hocki) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            return true;
        }
        return teacherService.getTeacherByUsername(authentication.getName())
                .map(teacher -> teacherService.getClazzesByMaGv(teacher.getMaGv()).stream()
                        .anyMatch(clazz -> clazz.getMaLop().equals(maLop) && clazz.getSemester().getName().equals(hocki)))
                .orElse(false);
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
        String studentName = attendance.getStudent() != null ? attendance.getStudent().getName() : null;
        String statusString = attendance.getStatus() != null ? attendance.getStatus().name() : null; // Lấy tên của enum

        return new AttendanceDTO(
                attendance.getAttendanceDate(),
                attendance.getSemester() != null ? attendance.getSemester().getName() : null,
                attendance.getClazz() != null ? attendance.getClazz().getMaLop() : null,
                attendance.getStudent() != null ? attendance.getStudent().getMssv() : null,
                statusString,
                studentName
        );
    }

    private Attendance convertToEntity(AttendanceRequest request) {
        Attendance attendance = new Attendance();
        attendance.setStatus(request.getStatus());
        attendance.setAttendanceDate(request.getAttendanceDate());
        if (request.getMssv() != null) {
            studentRepo.findByMssv(request.getMssv()).ifPresent(attendance::setStudent);
        }
        if (request.getHocKi() != null) {
            semesterRepo.findByName(request.getHocKi()).ifPresent(attendance::setSemester);
        }
        if (request.getMaLop() != null) {
            clazzRepo.findByMaLopAndSemester_Name(request.getMaLop(), request.getHocKi()).ifPresent(attendance::setClazz);
        }
        return attendance;
    }
}