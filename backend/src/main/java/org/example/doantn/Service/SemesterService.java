package org.example.doantn.Service;

import org.example.doantn.Entity.Semester;
import org.example.doantn.Repository.SemesterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SemesterService {

    @Autowired
    private SemesterRepo semesterRepo;

    // Lấy danh sách tất cả học kỳ
    public List<Semester> getAllSemesters() {
        return semesterRepo.findAll().stream()
                .filter(Semester::isOpen) // Sử dụng hàm isOpen()
                .collect(Collectors.toList());
    }
    public List<Semester> getAllSemestersNoFilter() {
        return semesterRepo.findAll();
    }

    // Lấy thông tin học kỳ theo ID
    public Semester getSemesterByName(String name) {
        return semesterRepo.findByName(name)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ có ID: " + name));
    }

    // Cập nhật trạng thái mở/đóng đăng ký lớp
    public Semester updateSemesterStatus(String name, boolean isOpen) {
        Semester semester = getSemesterByName(name);
        semester.setOpen(isOpen);
        return semesterRepo.save(semester);
    }

    // Tạo mới học kỳ
    public Semester createSemester(Semester semester) {
        return semesterRepo.save(semester);
    }

}