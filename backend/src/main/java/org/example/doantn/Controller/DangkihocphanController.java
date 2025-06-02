package org.example.doantn.Controller;
import org.example.doantn.Dto.request.DkhpRequest;
import org.example.doantn.Dto.response.CourseDTO;
import org.example.doantn.Dto.response.DkhpDTO;
import org.example.doantn.Entity.Course;
import org.example.doantn.Entity.Dangkihocphan;
import org.example.doantn.Entity.Semester;
import org.example.doantn.Entity.Student;
import org.example.doantn.Repository.CourseRepo;
import org.example.doantn.Repository.DangkihocphanRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Repository.StudentRepo;
import org.example.doantn.Service.DangkihocphanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dangkihocphan")
public class DangkihocphanController {

    @Autowired
    private DangkihocphanService dangkihocphanService;
    @Autowired
    private DangkihocphanRepo dangkihocphanRepo;

    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private SemesterRepo semesterRepo;
    @Autowired
    private CourseRepo courseRepo;

    @GetMapping
    public ResponseEntity<List<DkhpDTO>> getAllDangkihocphan() {
        List<DkhpDTO> result = dangkihocphanService.getAllDangkihocphan()
                .stream()
                .map(DkhpDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }


    @GetMapping("/{id}")
    public ResponseEntity<DkhpDTO> getDangkihocphanById(@PathVariable Integer id) {
        Dangkihocphan dangkihocphan = dangkihocphanService.getDangkihocphanById(id);
        if (dangkihocphan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new DkhpDTO(dangkihocphan));
    }


    @GetMapping("/{mssv}/{semesterName}")
    public ResponseEntity<List<DkhpDTO> > getDangkihocphanByMSSVAndSemesterName(@PathVariable String mssv,@PathVariable String semesterName) {
        Optional<Student> student = studentRepo.findByMssv(mssv);
        Optional<Semester> semester = semesterRepo.findByName(semesterName);

        List<DkhpDTO> registrations = dangkihocphanService.getDangkihocphanByMssvAndSemester(student.get().getMssv(), semester.get().getName())
                .stream()
                .map(DkhpDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(registrations);
    }


    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping
    public ResponseEntity<?> createDangkihocphan(
            @RequestBody DkhpRequest request,
            Authentication authentication) {
        try {
            Dangkihocphan dangkihocphan = convertToEntity(request, authentication.getName());
            Dangkihocphan savedDkhp = dangkihocphanService.createDangkihocphan(dangkihocphan);
            return ResponseEntity.ok(convertToDTO(savedDkhp));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDangkihocphan(@PathVariable Integer id, Authentication authentication) {
        String username = authentication.getName();
        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với username: " + username));

        Dangkihocphan dangkihocphan = dangkihocphanService.getDangkihocphanById(id);
        if (dangkihocphan == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bản ghi đăng ký học phần không tồn tại!");
        }
        if (!dangkihocphan.getStudent().getMssv().equals(student.getMssv())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền xóa đăng ký này!");
        }

        dangkihocphanService.deleteDangkihocphan(id);
        return ResponseEntity.ok("Xóa đăng ký học phần thành công!");
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/ctdt-courses")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getCtdtCourses(Authentication authentication) {
        String username = authentication.getName();
        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với username: " + username));

        // Lấy tất cả học phần trong CTĐT của sinh viên
        List<Course> allCourses = courseRepo.findByCtdt_maCtAndKhoa(student.getCtdt().getMaCt(),student.getBatch().getName());
        List<Map<String, Object>> allCourseDTOsWithGrades = new ArrayList<>();

        for (Course course : allCourses) {
            Map<String, Object> courseInfo = new HashMap<>();
            courseInfo.put("maHocPhan", course.getMaHocPhan());
            courseInfo.put("tenMonHoc", course.getName());
            courseInfo.put("soTinChi", course.getTinChi());
            courseInfo.put("khoiLuong", course.getKhoiLuong());
            courseInfo.put("suggestedSemester", course.getSuggestedSemester());
            courseInfo.put("gradeRatio", course.getGradeRatio());
            courseInfo.put("finalGrade", null);
            courseInfo.put("gradeLetter", null);

            // Tìm bản ghi Dangkihocphan mới nhất cho sinh viên và môn học này
            List<Dangkihocphan> enrollments = dangkihocphanRepo.findByStudent_MssvAndCourse_MaHocPhan(student.getMssv(), course.getMaHocPhan());
            Optional<Dangkihocphan> latestEnrollment = enrollments.stream()
                    .max(Comparator.comparing(dkhp -> dkhp.getSemester().getName()));
            if (latestEnrollment.isPresent()) {
                courseInfo.put("finalGrade", latestEnrollment.get().getFinalGrade());
                courseInfo.put("gradeLetter", latestEnrollment.get().getGradeLetter());
            }
            allCourseDTOsWithGrades.add(courseInfo);
        }

        // Lấy danh sách các bản ghi Dangkihocphan của sinh viên
        List<Dangkihocphan> registeredDkhps = dangkihocphanService.getDangkihocphanByMssv(student.getMssv());

        // Nhóm các Dangkihocphan theo maHocPhan và lấy bản ghi mới nhất theo tên học kỳ
        Map<String, Dangkihocphan> latestRegisteredDkhpMap = registeredDkhps.stream()
                .collect(Collectors.groupingBy(dkhp -> dkhp.getCourse().getMaHocPhan(),
                        Collectors.maxBy(Comparator.comparing(dkhp -> dkhp.getSemester().getName()))))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().orElse(null)));

        // Tạo danh sách học phần đã đăng ký với thông tin điểm (lấy từ bản ghi mới nhất)
        List<Map<String, Object>> registeredCoursesWithGrades = allCourses.stream()
                .filter(course -> latestRegisteredDkhpMap.containsKey(course.getMaHocPhan()))
                .map(course -> {
                    Map<String, Object> courseInfo = new HashMap<>();
                    Dangkihocphan latestDkhp = latestRegisteredDkhpMap.get(course.getMaHocPhan());
                    courseInfo.put("maHocPhan", course.getMaHocPhan());
                    courseInfo.put("tenMonHoc", course.getName());
                    courseInfo.put("soTinChi", course.getTinChi());
                    courseInfo.put("khoiLuong", course.getKhoiLuong());
                    courseInfo.put("suggestedSemester", course.getSuggestedSemester());
                    courseInfo.put("gradeRatio", course.getGradeRatio());
                    courseInfo.put("finalGrade", latestDkhp != null ? latestDkhp.getFinalGrade() : null);
                    courseInfo.put("gradeLetter", latestDkhp != null ? latestDkhp.getGradeLetter() : null);
                    return courseInfo;
                })
                .collect(Collectors.toList());

        // Tạo danh sách học phần chưa đăng ký (chỉ lấy thông tin môn học)
        List<Map<String, Object>> unregisteredCourses = allCourses.stream()
                .filter(course -> !latestRegisteredDkhpMap.containsKey(course.getMaHocPhan()))
                .map(course -> {
                    Map<String, Object> courseInfo = new HashMap<>();
                    courseInfo.put("maHocPhan", course.getMaHocPhan());
                    courseInfo.put("tenMonHoc", course.getName());
                    courseInfo.put("soTinChi", course.getTinChi());
                    courseInfo.put("khoiLuong", course.getKhoiLuong());
                    courseInfo.put("suggestedSemester", course.getSuggestedSemester());
                    courseInfo.put("gradeRatio", course.getGradeRatio());
                    return courseInfo;
                })
                .collect(Collectors.toList());

        Map<String, List<Map<String, Object>>> response = new HashMap<>();
        response.put("all", allCourseDTOsWithGrades);
        response.put("registered", registeredCoursesWithGrades);
        response.put("unregistered", unregisteredCourses);

        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/my-registrations/hocki/{semester}")
    public ResponseEntity<List<DkhpDTO>> getMyDangkihocphan(
            Authentication authentication,
            @PathVariable String semester) {
        String username = authentication.getName();
        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với username: " + username));

        List<DkhpDTO> registrations = dangkihocphanService.getDangkihocphanByMssvAndSemester(student.getMssv(), semester)
                .stream()
                .map(DkhpDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(registrations);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/missing-grades")
    public ResponseEntity<List<CourseDTO>> getMissingCourses(Authentication authentication) {
        // Lấy thông tin sinh viên từ tài khoản đăng nhập
        String username = authentication.getName();
        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với username: " + username));

        // Lấy tất cả học phần trong chương trình đào tạo của sinh viên
        List<Course> allCourses = courseRepo.findByCtdts_MaCt(student.getCtdt().getMaCt());

        // Lấy danh sách học phần sinh viên đã đăng ký và có điểm
        List<String> completedCourses = dangkihocphanService.getCompletedCourses(student.getMssv());

        // Lọc ra các học phần chưa có điểm
        List<CourseDTO> missingCourses = allCourses.stream()
                .filter(course -> !completedCourses.contains(course.getMaHocPhan())) // Lọc những học phần chưa có điểm
                .map(CourseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(missingCourses);
    }


    private Dangkihocphan convertToEntity(DkhpRequest request, String username) {
        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với username: " + username));

        Semester semester = semesterRepo.findByName(request.getSemesterName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học kỳ với tên: " + request.getSemesterName()));

        Course course = courseRepo.findByMaHocPhan(request.getMaHocPhan())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học phần với mã: " + request.getMaHocPhan()));

        Dangkihocphan dangkihocphan = new Dangkihocphan();
        dangkihocphan.setStudent(student);
        dangkihocphan.setCourse(course);
        dangkihocphan.setSemester(semester);
        // Các thuộc tính điểm (gki, cki, finalGrade, gradeLetter) sẽ là null khi tạo mới
        return dangkihocphan;
    }

    private DkhpDTO convertToDTO(Dangkihocphan dangkihocphan) {
        return new DkhpDTO(
                dangkihocphan.getCourse() != null ? dangkihocphan.getCourse().getMaHocPhan() : null,
                dangkihocphan.getStudent() != null ? dangkihocphan.getStudent().getMssv() : null,
                dangkihocphan.getSemester() != null ? dangkihocphan.getSemester().getName() : null,
                dangkihocphan.getFinalGrade(),
                dangkihocphan.getGradeLetter()
        );
    }
}