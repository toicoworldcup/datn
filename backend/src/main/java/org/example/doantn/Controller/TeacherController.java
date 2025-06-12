package org.example.doantn.Controller;

import com.fasterxml.jackson.databind.ObjectMapper; // <--- Cần thêm import này
import org.example.doantn.Dto.request.TeacherRequest;
import org.example.doantn.Dto.response.ClazzDTO;
import org.example.doantn.Dto.response.ScheduleDTO;
import org.example.doantn.Dto.response.TeacherDTO;
import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Schedule;
import org.example.doantn.Entity.Teacher;
import org.example.doantn.Repository.DepartmentRepo;
import org.example.doantn.Repository.TeacherRepo;
import org.example.doantn.Service.ScheduleService;
import org.example.doantn.Service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;
    @Autowired
    private DepartmentRepo departmentRepo;
    @Autowired
    private TeacherRepo teacherRepo;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired // <--- Inject ObjectMapper vào Controller
    private ObjectMapper objectMapper;


    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        List<TeacherDTO> teachers = teacherService.getAllTeachers()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teachers);
    }
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/myinfo")
    public ResponseEntity<TeacherDTO> getLoggedInTeacherInfo(Authentication authentication) {
        String username = authentication.getName();
        Optional<Teacher> teacher = teacherRepo.findByUser_Username(username);

        return teacher.map(value -> ResponseEntity.ok(convertToDTO(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping("/{maGv}")
    public ResponseEntity<TeacherDTO> getTeacherByMaGvForAdmin(@PathVariable String maGv) {
        Optional<Teacher> teacher = teacherRepo.findByMaGv(maGv);

        return teacher.map(value -> ResponseEntity.ok(convertToDTO(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }


    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addTeacher(@RequestBody TeacherRequest request) {
        try {
            Teacher teacher = convertToEntity(request);
            Teacher savedTeacher = teacherService.addTeacher(teacher);
            return ResponseEntity.ok(convertToDTO(savedTeacher));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    // Thay đổi tham số @RequestBody từ Teacher thành Map<String, Object>
    public ResponseEntity<TeacherDTO> updateTeacher(@PathVariable int id, @RequestBody Map<String, Object> payload) {
        try {
            // 1. Trích xuất tên khoa từ payload
            String departmentName = (String) payload.get("departmentName");

            // 2. Loại bỏ trường "department" (object) và "departmentName" (string) khỏi payload
            // để ObjectMapper có thể ánh xạ phần còn lại vào Teacher entity mà không bị lỗi.
            payload.remove("department"); // Xóa trường Department object nếu có
            payload.remove("departmentName"); // Xóa trường departmentName string

            // 3. Chuyển đổi phần còn lại của payload thành đối tượng Teacher
            Teacher updatedTeacherData = objectMapper.convertValue(payload, Teacher.class);

            // 4. Gọi service và truyền cả Teacher entity (với dữ liệu cơ bản) và tên khoa
            Teacher updatedAndSavedTeacher = teacherService.updateTeacher(id, updatedTeacherData, departmentName);

            // 5. Chuyển đổi kết quả sang DTO và trả về
            return ResponseEntity.ok(convertToDTO(updatedAndSavedTeacher));

        } catch (RuntimeException e) {
            // Xử lý các lỗi nghiệp vụ, ví dụ: "Department not found" từ Service
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Xử lý các lỗi hệ thống không mong muốn
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeacher(@PathVariable int id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok("Xóa giáo viên thành công!");
    }

    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping("/{teacherId}/class-count")
    public ResponseEntity<Integer> getClassCount(@PathVariable int teacherId) {
        int count = teacherService.getNumberOfClassesByTeacher(teacherId);
        return ResponseEntity.ok(count);
    }
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/clazzes/{semesterName}")
    public ResponseEntity<Set<ClazzDTO>> getClazzesByTeacher(
            Authentication authentication, @PathVariable String semesterName) {

        String username = authentication.getName();
        Optional<Teacher> teacher = teacherService.getTeacherByUsername(username);
        String maGv = teacher.get().getMaGv();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            // Nếu là ADMIN, lấy tất cả các lớp của tất cả giáo viên trong học kỳ đó.
            Set<Clazz> clazzes = teacherService.getClazzesByMaGvAndSemesterName(maGv, semesterName);
            Set<ClazzDTO> clazzDTOs = clazzes.stream()
                    .map(ClazzDTO::new)
                    .collect(Collectors.toSet());
            return ResponseEntity.ok(clazzDTOs);
        }

        Set<Clazz> clazzes = teacherService.getClazzesByMaGvAndSemesterName(maGv, semesterName);
        Set<ClazzDTO> clazzDTOs = clazzes.stream()
                .map(ClazzDTO::new)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(clazzDTOs);
    }

    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/course/{semesterName}")
    public ResponseEntity<Set<String>> getModulesByTeacherAndSemester(
            Authentication authentication, @PathVariable String semesterName) {
        String username = authentication.getName();
        Optional<Teacher> teacher = teacherService.getTeacherByUsername(username);

        if (teacher.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String maGv = teacher.get().getMaGv();
        Set<String> modules = teacherService.getModulesByMaGvAndSemester(maGv, semesterName);
        return ResponseEntity.ok(modules);
    }
    @GetMapping("/teacher/semester/{semesterName}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<ScheduleDTO>> getTeacherScheduleBySemester(Authentication authentication, @PathVariable String semesterName) {
        // Lấy thông tin người dùng đang đăng nhập
        String username = authentication.getName(); // Lấy username
        Optional<Teacher> teacherOptional = teacherService.getTeacherByUsername(username);

        if (teacherOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Teacher teacher = teacherOptional.get();
        String maGv = teacher.getMaGv();

        // Gọi service để lấy thời khóa biểu của giáo viên
        List<Schedule> schedules = scheduleService.getScheduleByTeacherAndSemester(maGv, semesterName);
        List<ScheduleDTO> scheduleDTOs = schedules.stream()
                .map(schedule -> new ScheduleDTO(
                        schedule.getClazz().getMaLop(),
                        schedule.getRoom().getName(),
                        schedule.getTimeSlot().getName(),
                        schedule.getDayOfWeek(),
                        schedule.getSemester().getName(),
                        schedule.getClazz().getCourse().getName()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(scheduleDTOs);
    }


    @PostMapping("/import")
    @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    public ResponseEntity<?> importTeachers(@RequestParam("file") MultipartFile file) {
        try {
            teacherService.processTeacherExcel(file);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã nhập giáo viên thành công.");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi xử lý file Excel: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> payload) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy username của giáo viên đã đăng nhập

        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");

        if (teacherService.changePassword(username, oldPassword, newPassword)) {
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Đổi mật khẩu thất bại. Mật khẩu cũ không đúng.");
        }
    }


    private TeacherDTO convertToDTO(Teacher teacher) {
        return new TeacherDTO(
                teacher.getId(),
                teacher.getAddress(),
                teacher.getCccd(),
                teacher.getDateOfBirth(),
                // Đảm bảo rằng getDepartment() trả về Department object và bạn có thể gọi getName()
                teacher.getDepartment() != null ? teacher.getDepartment().getName() : null,
                teacher.getEmail(),
                teacher.getGender(),
                teacher.getMaGv(),
                teacher.getName(),
                teacher.getPhoneNumber()
        );
    }
    private Teacher convertToEntity(TeacherRequest request) {
        Teacher teacher = new Teacher();
        teacher.setName(request.getName());
        teacher.setMaGv(request.getMaGv());
        teacher.setEmail(request.getEmail());
        teacher.setPhoneNumber(request.getPhoneNumber());
        teacher.setCccd(request.getCccd());
        teacher.setDateOfBirth(request.getDateOfBirth());
        teacher.setAddress(request.getAddress());
        teacher.setGender(request.getGender());
        // Khi tạo mới, tìm Department bằng tên từ request
        if (request.getDepartmentName() != null && !request.getDepartmentName().trim().isEmpty()) {
            departmentRepo.findByName(request.getDepartmentName())
                    .ifPresent(teacher::setDepartment);
            // Hoặc nếu bạn muốn báo lỗi khi không tìm thấy:
            // .orElseThrow(() -> new IllegalArgumentException("Department not found: " + request.getDepartmentName()));
        } else {
            teacher.setDepartment(null);
        }
        return teacher;
    }

}