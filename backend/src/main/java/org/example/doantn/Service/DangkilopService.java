package org.example.doantn.Service;

import org.example.doantn.Entity.*;
import org.example.doantn.Repository.ClazzRepo;
import org.example.doantn.Repository.DangkilopRepo;
import org.example.doantn.Repository.ScheduleRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Repository.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DangkilopService {
    @Autowired
    private DangkilopRepo dangkilopRepo;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private ClazzRepo clazzRepo;
    @Autowired
    private SemesterRepo semesterRepo;
    @Autowired
    private ScheduleRepo scheduleRepo;

    public Dangkilop getDangkilopById(Integer id) {
        return dangkilopRepo.findById(id).orElseThrow(() -> new RuntimeException("Đăng ký không tồn tại"));
    }

    @Transactional
    public Dangkilop registerClass(Dangkilop dangkilop) {
        Student student = studentRepo.findByMssv(dangkilop.getStudent().getMssv())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với MSSV."));

        Clazz clazzToRegister = clazzRepo.findByMaLopAndSemester_Name(dangkilop.getClazz().getMaLop(), dangkilop.getSemester().getName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp."));

        Semester semester = semesterRepo.findByName(dangkilop.getSemester().getName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kỳ học."));

        if (!semester.isOpen()) {
            throw new RuntimeException("Kỳ học này chưa mở đăng ký.");
        }

        boolean exists = dangkilopRepo.existsByStudentAndClazz(student, clazzToRegister);
        if (exists) {
            throw new RuntimeException("Bạn đã đăng ký lớp này trước đó.");
        }

        // Kiểm tra xung đột lịch
        if (isScheduleConflict(student.getMssv(), clazzToRegister.getMaLop(), semester.getName())) {
            throw new RuntimeException("Lớp học này bị trùng lịch với thời khóa biểu của bạn.");
        }

        dangkilop.setClazz(clazzToRegister);
        dangkilop.setSemester(semester);

        Dangkilop savedDangkilop = dangkilopRepo.save(dangkilop);

        // Tăng số lượng sinh viên của lớp
        clazzToRegister.setSoLuongSinhVien(clazzToRegister.getSoLuongSinhVien() + 1);
        clazzRepo.save(clazzToRegister);

        return savedDangkilop;
    }

    @Transactional
    public void bulkDeleteDangkilop(List<Integer> ids, String studentMssv) {
        for (Integer id : ids) {
            Dangkilop dangkilop = dangkilopRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đăng ký với ID: " + id));
            if (!dangkilop.getStudent().getMssv().equals(studentMssv)) {
                throw new IllegalArgumentException("Bạn không có quyền xóa đăng ký này.");
            }
            Clazz clazz = dangkilop.getClazz();
            dangkilopRepo.deleteById(id);
            // Giảm số lượng sinh viên của lớp
            clazz.setSoLuongSinhVien(clazz.getSoLuongSinhVien() - 1);
            clazzRepo.save(clazz);
        }
    }

    // Hủy đăng ký lớp
    @Transactional
    public void deleteDangkilop(Integer id) {
        Dangkilop dangkilop = getDangkilopById(id);
        Clazz clazz = dangkilop.getClazz();
        dangkilopRepo.delete(dangkilop);
        // Giảm số lượng sinh viên của lớp
        clazz.setSoLuongSinhVien(clazz.getSoLuongSinhVien() - 1);
        clazzRepo.save(clazz);
    }

    public List<Dangkilop> getDangkilopByMssvAndSemester(String mssv, String semester) {
        return dangkilopRepo.findByStudent_MssvAndSemesterName(mssv, semester);
    }

    public boolean isScheduleConflict(String mssv, String maLopToRegister, String semesterName) {
        // Lấy thời khóa biểu của sinh viên trong học kỳ
        List<Schedule> studentSchedules = scheduleRepo.findSchedulesByStudentMssvAndSemesterName(mssv, semesterName);

        // Lấy lịch học của lớp muốn đăng ký
        List<Schedule> classSchedulesToRegister = scheduleRepo.findByClazz_MaLopAndSemester_Name(maLopToRegister, semesterName);

        for (Schedule studentSchedule : studentSchedules) {
            for (Schedule classSchedule : classSchedulesToRegister) {
                // Kiểm tra xem có trùng ngày và ca học không
                if (studentSchedule.getDayOfWeek().equals(classSchedule.getDayOfWeek()) &&
                        studentSchedule.getTimeSlot().getName().equals(classSchedule.getTimeSlot().getName())) {
                    return true; // Có xung đột lịch
                }
            }
        }
        return false; // Không có xung đột lịch
    }
}