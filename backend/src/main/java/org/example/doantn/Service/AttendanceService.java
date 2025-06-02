package org.example.doantn.Service;

import org.example.doantn.Entity.*;
import org.example.doantn.Repository.AttendanceRepo;
import org.example.doantn.Repository.ClazzRepo;
import org.example.doantn.Repository.DangkilopRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceService {

    @Autowired
    private AttendanceRepo attendanceRepo;
    @Autowired
    private DangkilopRepo dangkilopRepo;
    @Autowired
    private SemesterRepo semesterRepo;
    @Autowired
    private ClazzRepo clazzRepo;

    public List<Attendance> getAllAttendances() {
        return attendanceRepo.findAll();
    }

    // Lấy điểm danh theo ngày cụ thể cho một sinh viên
    public Attendance getAttendanceByClazzAndStudentAndSemester(String maLop, String mssv, String hocki, String attendanceDate) {
        List<Attendance> attendances = attendanceRepo.findByClazz_MaLopAndStudent_MssvAndSemester_NameAndAttendanceDate(maLop, mssv, hocki, LocalDate.parse(attendanceDate));
        return attendances.isEmpty() ? null : attendances.get(0); // Trả về Attendance hoặc null nếu không tìm thấy
    }

    // Kiểm tra xem điểm danh cho lớp và ngày đã tồn tại chưa
    public boolean checkAttendanceExists(String maLop, String hocKi, LocalDate date) {
        Clazz clazz = clazzRepo.findByMaLop(maLop).orElse(null);
        Semester semester = semesterRepo.findByName(hocKi).orElse(null);
        if (clazz != null && semester != null) {
            return attendanceRepo.existsByClazzAndSemesterAndAttendanceDate(clazz, semester, date);
        }
        return false;
    }

    public Attendance markAttendance(Attendance attendance) {
        Dangkilop dangkilop = dangkilopRepo.findByClazzMaLopAndSemesterNameAndMssv(attendance.getClazz().getMaLop(), attendance.getSemester().getName(), attendance.getStudent().getMssv())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên  "));
        return attendanceRepo.save(attendance);
    }

    // Bulk mark attendance
    public List<Attendance> markBulkAttendance(List<Attendance> attendances) {
        return attendanceRepo.saveAll(attendances);
    }

    public int getAbsentCount(String maLop, String mssv, LocalDate toDate) {
        return attendanceRepo.countByClazz_MaLopAndStudent_MssvAndStatusAndAttendanceDateLessThanEqual(
                maLop, mssv, AStatus.VANG, toDate
        );
    }

    public List<Attendance> getAttendanceByClazzAndSemesterAndDate(String maLop, String hocKi, LocalDate date) {
        Clazz clazz = clazzRepo.findByMaLop(maLop).orElse(null);
        Semester semester = semesterRepo.findByName(hocKi).orElse(null);
        if (clazz != null && semester != null) {
            return attendanceRepo.findByClazzAndSemesterAndAttendanceDate(clazz, semester, date);
        }
        return Collections.emptyList();
    }

    // New method to get all unique attendance dates for a class and semester
    public List<String> getAttendanceDatesByClazzAndSemester(String maLop, String hocKi) {
        Clazz clazz = clazzRepo.findByMaLop(maLop).orElse(null);
        Semester semester = semesterRepo.findByName(hocKi).orElse(null);
        if (clazz != null && semester != null) {
            return attendanceRepo.findByClazzAndSemester(clazz, semester).stream()
                    .map(Attendance::getAttendanceDate)
                    .distinct()
                    .map(LocalDate::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}