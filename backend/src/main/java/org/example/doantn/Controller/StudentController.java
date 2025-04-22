package org.example.doantn.Controller;

import org.example.doantn.Dto.request.StudentRequest;
import org.example.doantn.Dto.response.StudentDTO;
import org.example.doantn.Entity.Student;
import org.example.doantn.Import.StudentImport;
import org.example.doantn.Export.StudentExport;
import org.example.doantn.Repository.*;
import org.example.doantn.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentImport studentImport;

    @Autowired
    private StudentExport studentExport;

    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private BatchRepo batchRepo;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CtdtRepo ctdtRepo;
    @Autowired
    private StudentRepo studentRepo;

    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> studentDTOs = studentService.getAllStudents()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }

    //@PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/{mssv}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable String mssv, Authentication authentication) {
        String username = authentication.getName();
        Optional<Student> student = studentService.getStudentByUsername(username);

        if (student.isEmpty() || !Objects.equals(student.get().getMssv(), mssv)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(convertToDTO(student.get()));
    }


    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search/{mssv}")
    public ResponseEntity<StudentDTO> searchStudentByMssv(@PathVariable String mssv) {
        Optional<Student> student = studentService.getStudentByMssv(mssv);

        if (!student.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(convertToDTO(student.get()));
    }

    //@PreAuthorize("hasRole('ADMIN')")
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



    // @PreAuthorize("hasRole('ADMIN')")
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

   // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Integer id, @RequestBody Student studentDetails) {
        Student updatedStudent = studentService.updateStudent(id, studentDetails);
        return ResponseEntity.ok(convertToDTO(updatedStudent));
    }

   // @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Xóa sinh viên thành công");
    }

   // @PreAuthorize("hasRole('ADMIN')")
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

   // @PreAuthorize("hasRole('ADMIN')")
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
}
