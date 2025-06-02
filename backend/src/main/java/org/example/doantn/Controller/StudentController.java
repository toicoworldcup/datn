package org.example.doantn.Controller;

import org.example.doantn.Dto.response.CthDTO;
import org.example.doantn.Dto.response.GraduationResultDTO;
import org.example.doantn.Dto.response.ScheduleDTO;
import org.example.doantn.Dto.response.StudentDTO;
import org.example.doantn.Dto.request.StudentRequest;
import org.example.doantn.Entity.Schedule;
import org.example.doantn.Entity.Student;
import org.example.doantn.Export.StudentExport;
import org.example.doantn.Import.StudentImport;
import org.example.doantn.Repository.*;
import org.example.doantn.Service.ScheduleService;
import org.example.doantn.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private StudentImport studentImport;

    @Autowired
    private StudentExport studentExport;

    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private BatchRepo batchRepo;

    @Autowired
    private CtdtRepo ctdtRepo;
    @Autowired
    private StudentRepo studentRepo;

    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> studentDTOs = studentService.getAllStudents()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{mssv}")
    public ResponseEntity<StudentDTO> getStudentByMssv(@PathVariable String mssv, Authentication authentication) {
        String username = authentication.getName();
        Optional<Student> student = studentService.getStudentByMssv(mssv);

        if (student.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Nếu là STUDENT thì chỉ được xem chính mình
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            Optional<Student> currentStudent = studentService.getStudentByUsername(username);
            if (currentStudent.isEmpty() || !Objects.equals(currentStudent.get().getMssv(), mssv)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return ResponseEntity.ok(convertToDTO(student.get()));
    }

    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping("/search/{mssv}")
    public ResponseEntity<StudentDTO> searchStudentByMssv(@PathVariable String mssv) {
        Optional<Student> student = studentService.getStudentByMssv(mssv);

        return student.map(value -> ResponseEntity.ok(convertToDTO(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me")
    public ResponseEntity<StudentDTO> getCurrentStudent(Authentication authentication) {
        String username = authentication.getName();
        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với username: " + username));

        return ResponseEntity.ok(convertToDTO(student));
    }

    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping("/search/batch/{batch}")
    public ResponseEntity<List<StudentDTO>> searchStudentsByBatch(@PathVariable String batch) {
        // Gọi dịch vụ để tìm kiếm sinh viên theo khóa
        List<Student> students = studentRepo.findByBatchName(batch);

        // Nếu không có sinh viên nào trong khóa học đó
        if (students.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Chuyển danh sách sinh viên sang danh sách DTO và trả về kết quả
        List<StudentDTO> studentDTOs = students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(studentDTOs);
    }

    @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping("/search/by-ctdt-and-batch/{ctdtName}/{batchName}")
    public ResponseEntity<List<StudentDTO>> getStudentsByCtdtNameAndBatch(@PathVariable  String ctdtName,@PathVariable  String batchName) {
        List<Student> students = studentService.getStudentsByCtdtNameAndBatch(ctdtName, batchName);

        if (students.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // Chuyển danh sách sinh viên sang danh sách DTO và trả về kết quả
        List<StudentDTO> studentDTOs = students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(studentDTOs);
    }

    @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping("/search/by-code/{maCt}")
    public ResponseEntity<List<StudentDTO>> getStudentsByCodename(@PathVariable String maCt) {
        List<Student> students = studentService.getStudentsByCtdtName(maCt);
        // Nếu không có sinh viên nào trong khóa học đó
        if (students.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // Chuyển danh sách sinh viên sang danh sách DTO và trả về kết quả
        List<StudentDTO> studentDTOs = students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(studentDTOs);
    }

    @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody StudentRequest request) {
        try {
            Student student = convertToEntity(request);
            Student savedStudent = studentService.addStudent(student);
            return ResponseEntity.ok(convertToDTO(savedStudent));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Integer id, @RequestBody Student studentDetails) {
        Student updatedStudent = studentService.updateStudent(id, studentDetails);
        return ResponseEntity.ok(convertToDTO(updatedStudent));
    }

    @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Xóa sinh viên thành công");
    }

    @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @PostMapping("/import")
    public ResponseEntity<String> importStudents(@RequestParam("file") MultipartFile file) {
        try {
            studentImport.importStudentsFromExcel(file);
            return ResponseEntity.ok("Import dữ liệu sinh viên thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Import thất bại: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping("/export")
    public void exportStudents(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=students.xlsx");

            studentExport.exportStudentsToExcel(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me/xet-tot-nghiep")
    public ResponseEntity<GraduationResultDTO> xemXetTotNghiep(Authentication authentication) {
        String username = authentication.getName();
        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với username: " + username));
        GraduationResultDTO result = studentService.xetTotNghiep(student.getMssv());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/schedule/semester/{semesterName}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ScheduleDTO>> getMyScheduleBySemester(Authentication authentication, @PathVariable String semesterName) {
        // Lấy username của sinh viên đang đăng nhập
        String username = authentication.getName();
        Optional<Student> studentOptional = studentService.getStudentByUsername(username);

        if (studentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Student student = studentOptional.get();
        String mssv = student.getMssv();

        // Gọi service để lấy thời khóa biểu của sinh viên
        List<Schedule> schedules = scheduleService.getScheduleByStudentAndSemester(mssv, semesterName);
        List<ScheduleDTO> scheduleDTOs = schedules.stream()
                .map(schedule -> new ScheduleDTO(
                        schedule.getClazz().getMaLop(),
                        schedule.getRoom().getName(),
                        schedule.getTimeSlot().getName(),
                        schedule.getDayOfWeek(),
                        schedule.getSemester().getName()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(scheduleDTOs);
    }

    @GetMapping("/me/chuong-trinh-dao-tao-va-diem")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CthDTO>> getMyChuongTrinhDaoTaoVaDiem(Authentication authentication) {
        String username = authentication.getName();
        List<CthDTO> cthDTOs = studentService.getChuongTrinhDaoTaoVaDiem(username);
        return ResponseEntity.ok(cthDTOs);
    }


    private StudentDTO convertToDTO(Student student) {
        return new StudentDTO(
                student.getAddress(),
                student.getBatch() != null ? student.getBatch().getName() : null,
                student.getCccd(),
                student.getDateOfBirth(),
                student.getDepartment() != null ? student.getDepartment().getName() : null,
                student.getEmail(),
                student.getGender(),
                student.getMssv(),
                student.getName(),
                student.getPhone(),
                student.getCtdt() != null ? student.getCtdt().getName() : null

        );
    }
    private Student convertToEntity(StudentRequest request) {
        Student student = new Student();
        student.setName(request.getName());
        student.setMssv(request.getMssv());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setCccd(request.getCccd());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAddress(request.getAddress());
        student.setGender(request.getGender());
        if (request.getBatchName() != null) {
            batchRepo.findByName(request.getBatchName()).ifPresent(student::setBatch);
        }
        if (request.getMaCt() != null) {
            ctdtRepo.findByMaCt(request.getMaCt()).ifPresent(student::setCtdt);
        }
        if (request.getDepartmentName() != null) {
            departmentRepo.findByName(request.getDepartmentName()).ifPresent(student::setDepartment);
        }
        return student;
    }
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> payload) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy username của sinh viên đã đăng nhập

        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");

        if (studentService.changePassword(username, oldPassword, newPassword)) {
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Đổi mật khẩu thất bại. Mật khẩu cũ không đúng.");
        }
    }
}