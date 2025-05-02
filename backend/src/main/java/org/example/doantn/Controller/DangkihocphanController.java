package org.example.doantn.Controller;
import org.example.doantn.Dto.request.DkhpRequest;
import org.example.doantn.Dto.response.CourseDTO;
import org.example.doantn.Dto.response.DkhpDTO;
import org.example.doantn.Entity.Course;
import org.example.doantn.Entity.Dangkihocphan;
import org.example.doantn.Entity.Semester;
import org.example.doantn.Entity.Student;
import org.example.doantn.Repository.CourseRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Repository.StudentRepo;
import org.example.doantn.Service.DangkihocphanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dangkihocphan")
public class DangkihocphanController {

    @Autowired
    private DangkihocphanService dangkihocphanService;

    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private SemesterRepo semesterRepo;
    @Autowired
    private CourseRepo courseRepo;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<DkhpDTO>> getAllDangkihocphan() {
        List<DkhpDTO> result = dangkihocphanService.getAllDangkihocphan()
                .stream()
                .map(DkhpDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<DkhpDTO> getDangkihocphanById(@PathVariable Integer id) {
        Dangkihocphan dangkihocphan = dangkihocphanService.getDangkihocphanById(id);
        if (dangkihocphan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new DkhpDTO(dangkihocphan));
    }


    @PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<Map<String, List<CourseDTO>>> getCtdtCourses(Authentication authentication) {
        String username = authentication.getName();
        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với username: " + username));

        // Lấy tất cả học phần trong CTĐT của sinh viên
        List<Course> allCourses = courseRepo.findByCtdts_MaCt(student.getCtdt().getMaCt());

        // Lấy danh sách học phần sinh viên đã đăng ký
        List<String> registeredCourseCodes = dangkihocphanService.getDangkihocphanByMssv(student.getMssv())
                .stream()
                .map(dkhp -> dkhp.getCourse().getMaHocPhan())
                .collect(Collectors.toList());

        // Phân loại học phần đã và chưa đăng ký
        List<CourseDTO> registeredCourses = allCourses.stream()
                .filter(course -> registeredCourseCodes.contains(course.getMaHocPhan()))
                .map(CourseDTO::new)
                .collect(Collectors.toList());

        List<CourseDTO> unregisteredCourses = allCourses.stream()
                .filter(course -> !registeredCourseCodes.contains(course.getMaHocPhan()))
                .map(CourseDTO::new)
                .collect(Collectors.toList());

        Map<String, List<CourseDTO>> response = new HashMap<>();
        response.put("registered", registeredCourses);
        response.put("unregistered", unregisteredCourses);

        return ResponseEntity.ok(response);
    }




    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/my-registrations")
    public ResponseEntity<List<DkhpDTO>> getMyDangkihocphan(
            Authentication authentication,
            @RequestParam String semester) {
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
        return dangkihocphan;
    }

    private DkhpDTO convertToDTO(Dangkihocphan dangkihocphan) {
        return new DkhpDTO(
                dangkihocphan.getSemester() != null ? dangkihocphan.getSemester().getName() : null,
                dangkihocphan.getStudent() != null ? dangkihocphan.getStudent().getMssv() : null,
                dangkihocphan.getCourse() != null ? dangkihocphan.getCourse().getMaHocPhan() : null
        );
    }
}
