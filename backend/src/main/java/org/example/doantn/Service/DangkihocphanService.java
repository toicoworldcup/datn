package org.example.doantn.Service;

import org.example.doantn.Entity.Course;
import org.example.doantn.Entity.Dangkihocphan;
import org.example.doantn.Entity.Semester;
import org.example.doantn.Entity.Student;
import org.example.doantn.Repository.CourseRepo;
import org.example.doantn.Repository.DangkihocphanRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Repository.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học phần với mã: "));

        Semester semester = semesterRepo.findByName(dangkihocphan.getSemester().getName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kỳ học với tên: "));

        Student student = studentRepo.findById(dangkihocphan.getStudent().getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với ID: " + dangkihocphan.getStudent().getId()));

        // Kiểm tra học phần có trong chương trình đào tạo của sinh viên
        if (!student.getCtdt().getCourses().contains(course)) {
            throw new IllegalArgumentException("Học phần không thuộc chương trình đào tạo của sinh viên.");
        }

        if (!semester.isOpen()) {
            throw new IllegalArgumentException("Lỗi: học kỳ chưa mở");
        }

        dangkihocphan.setCourse(course);
        dangkihocphan.setSemester(semester);
        dangkihocphan.setStudent(student);

        return dangkihocphanRepo.save(dangkihocphan);
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
    public List<Map<String, Object>> getAccumulatedGrades(String mssv) { // Tên phương thức đã đổi
        Student student = studentRepo.findByMssv(mssv)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với MSSV: " + mssv));

        List<Dangkihocphan> allStudentEnrollments = dangkihocphanRepo.findByStudent_Mssv(mssv);

        List<Map<String, Object>> cumulativeCourseResults = new ArrayList<>();

        Map<String, Optional<Dangkihocphan>> latestEnrollmentsByCourse = allStudentEnrollments.stream()
                .filter(dkhp -> dkhp.getFinalGrade() != null)
                .collect(Collectors.groupingBy(
                        dkhp -> dkhp.getCourse().getMaHocPhan(),
                        Collectors.maxBy(Comparator.comparing(dkhp -> dkhp.getSemester().getName()))
                ));

        for (Map.Entry<String, Optional<Dangkihocphan>> entry : latestEnrollmentsByCourse.entrySet()) {
            if (entry.getValue().isPresent()) {
                Dangkihocphan dkhp = entry.getValue().get();
                Map<String, Object> courseResult = new HashMap<>();
                courseResult.put("maHocPhan", dkhp.getCourse().getMaHocPhan());
                courseResult.put("tenMonHoc", dkhp.getCourse().getName());
                courseResult.put("soTinChi", dkhp.getCourse().getTinChi());
                courseResult.put("finalGrade", dkhp.getFinalGrade());
                courseResult.put("gradeLetter", dkhp.getGradeLetter());

                cumulativeCourseResults.add(courseResult);
            }
        }
        return cumulativeCourseResults;
    }
}