package org.example.doantn.Service;

import org.example.doantn.Entity.Course;
import org.example.doantn.Entity.Dangkihocphan;
import org.example.doantn.Entity.Semester;
import org.example.doantn.Entity.Student;
import org.example.doantn.Repository.DangkihocphanRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Repository.StudentRepo;
import org.example.doantn.Repository.CourseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DangkihocphanService {
    @Autowired
    private DangkihocphanRepo dangkihocphanRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private SemesterRepo semesterRepo;

    public List<Dangkihocphan> getAllDangkihocphan() {
        return dangkihocphanRepo.findAll();
    }

    public Dangkihocphan getDangkihocphanById(Integer id) {
        return dangkihocphanRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đăng ký học phần với ID: " + id));
    }
        public Dangkihocphan createDangkihocphan(Dangkihocphan dangkihocphan) {
            Course course = courseRepo.findByMaHocPhan(dangkihocphan.getCourse().getMaHocPhan())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học phần với mã: " ));
            Semester semester = semesterRepo.findByName(dangkihocphan.getSemester().getName())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kỳ học với tên: "));
            dangkihocphan.setCourse(course);
            if (!semester.isOpen()) {
                throw new IllegalArgumentException("Lỗi: học kì chưa mở");
            }
            dangkihocphan.setSemester(semester);

            return dangkihocphanRepo.save(dangkihocphan);
        }



    public Dangkihocphan updateDangkihocphan(Integer id, String username, Integer courseId) {
            Dangkihocphan existingDangkihocphan = dangkihocphanRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đăng ký học phần với ID: " + id));

            Student student = studentRepo.findByUser_Username(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với username: " + username));

            if (!existingDangkihocphan.getStudent().getId().equals(student.getId())) {
                throw new RuntimeException("Bạn không có quyền cập nhật đăng ký này!");
            }

            if (courseId != null) {
                existingDangkihocphan.setCourse(courseRepo.findById(courseId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy học phần với ID: " + courseId)));
            }

            return dangkihocphanRepo.save(existingDangkihocphan);
        }



    public void deleteDangkihocphan(Integer id) {
        if (!dangkihocphanRepo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy đăng ký học phần với ID: " + id);
        }
        dangkihocphanRepo.deleteById(id);
    }
    public List<Dangkihocphan> getDangkihocphanByMssv(String mssv) {
        Student student = studentRepo.findByMssv(mssv)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với MSSV: " + mssv));

        return dangkihocphanRepo.findByStudent(student);
    }
    public List<Dangkihocphan> getDangkihocphanByMssvAndSemester(String mssv, String semester) {
        return dangkihocphanRepo.findByStudent_MssvAndSemesterName(mssv, semester);
    }

    public List<String> getCompletedCourses(String mssv) {
        return dangkihocphanRepo.findByStudent_Mssv(mssv).stream()
                .filter(dkhp -> dkhp.getGki() != null || dkhp.getCki() != null) // Chỉ lấy những học phần đã có điểm
                .map(dkhp -> dkhp.getCourse().getMaHocPhan()) // Chuyển thành danh sách mã học phần
                .collect(Collectors.toList());
    }



}
