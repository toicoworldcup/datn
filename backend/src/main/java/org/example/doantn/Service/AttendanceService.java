package org.example.doantn.Service;

import org.example.doantn.Entity.Attendance;
import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Dangkilop;
import org.example.doantn.Entity.Student;
import org.example.doantn.Repository.AttendanceRepo;
import org.example.doantn.Repository.DangkilopRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepo attendanceRepo;
    @Autowired
    private DangkilopRepo dangkilopRepo;

    public List<Attendance> getAllAttendances() {
        return attendanceRepo.findAll();
    }

    public List<Attendance> getAttendanceByClazzAndDate(Integer clazzId, LocalDate date) {
        return attendanceRepo.findByClazzIdAndDate(clazzId, date);
    }
    public List<Attendance> getAttendanceByClazzAndStudentAndSemester(String maLop, String mssv,String hocki) {
        return attendanceRepo.findByClazz_MaLopAndStudent_MssvAndSemeter_Name(maLop, mssv,hocki);
    }

    public Attendance markAttendance(Attendance attendance) {
        Dangkilop dangkilop = dangkilopRepo.findByClazzMaLopAndSemesterNameAndMssv(attendance.getClazz().getMaLop(), attendance.getSemester().getName(), attendance.getStudent().getMssv())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên  " ));
        return attendanceRepo.save(attendance);
    }

    public Optional<Attendance> getAttendanceById(Integer id) {
        return attendanceRepo.findById(id);
    }

    public Attendance updateAttendance(Integer id, Attendance updatedAttendance) {
        return attendanceRepo.findById(id).map(attendance -> {
            attendance.setStatus(updatedAttendance.getStatus());
            attendance.setAttendanceDate(updatedAttendance.getAttendanceDate());
            attendance.setClazz(updatedAttendance.getClazz());
            attendance.setStudent(updatedAttendance.getStudent());
            return attendanceRepo.save(attendance);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy điểm danh với ID: " + id));
    }

    public void deleteAttendance(Integer id) {
        if (attendanceRepo.existsById(id)) {
            attendanceRepo.deleteById(id);
        } else {
            throw new RuntimeException("Không tìm thấy điểm danh với ID: " + id);
        }
    }
}
