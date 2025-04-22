package org.example.doantn.Controller;

import org.apache.poi.ss.formula.functions.T;
import org.example.doantn.Dto.request.AttendanceRequest;
import org.example.doantn.Dto.request.StudentRequest;
import org.example.doantn.Dto.response.AttendanceDTO;
import org.example.doantn.Dto.response.StudentDTO;
import org.example.doantn.Entity.*;
import org.example.doantn.Repository.ClazzRepo;
import org.example.doantn.Repository.DangkilopRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Repository.StudentRepo;
import org.example.doantn.Service.AttendanceService;
import org.example.doantn.Service.ClazzService;
import org.example.doantn.Service.TeacherService;
import org.example.doantn.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private ClazzService clazzService;

    @Autowired
    private UserService userService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private SemesterRepo semesterRepo;
    @Autowired
    private ClazzRepo clazzRepo;
    @Autowired
    private DangkilopRepo dangkilopRepo;
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AttendanceDTO>> getAllAttendances() {
        List<AttendanceDTO> responses = attendanceService.getAllAttendances().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/clazz/{maLop}/student/{mssv}/semester/{hocki}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByClazzAndStudentAndSemester(
            @PathVariable String maLop,
            @PathVariable String mssv,
            @PathVariable String hocki,
            Authentication authentication) {
        if (!hasAccess(authentication, maLop,hocki)) {
            return ResponseEntity.status(403).build();
        }
        List<AttendanceDTO> responses = attendanceService.getAttendanceByClazzAndStudentAndSemester(maLop, mssv,hocki).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
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
            Attendance attendance = convertToEntity(request);
            Attendance savedAttendance = attendanceService.markAttendance(attendance);
            return ResponseEntity.ok(convertToDTO(savedAttendance));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
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
        return new AttendanceDTO(
                attendance.getAttendanceDate(),
                attendance.getSemester() != null ? attendance.getSemester().getName() : null,
                attendance.getClazz() != null ? attendance.getClazz().getMaLop() : null,
                attendance.getStudent() != null ? attendance.getStudent().getMssv() : null,
                attendance.getStatus()

        );
    }
    private Attendance convertToEntity(AttendanceRequest request) {
        Attendance attendance = new Attendance();
        attendance.setStatus(request.getStatus());
        attendance.setAttendanceDate(request.getAttendanceDate());
        // Tìm batch theo tên từ repo và set vào student
        if (request.getMssv() != null) {
            studentRepo.findByMssv(request.getMssv()).ifPresent(attendance::setStudent);
        }

        // Tìm department theo tên từ repo và set vào student
        if (request.getHocKi() != null) {
            semesterRepo.findByName(request.getHocKi()).ifPresent(attendance::setSemester);
        }
        if (request.getMaLop() != null) {
            clazzRepo.findByMaLopAndSemester_Name(request.getMaLop(),request.getHocKi()).ifPresent(attendance::setClazz);
        }
        return attendance;
    }


}
