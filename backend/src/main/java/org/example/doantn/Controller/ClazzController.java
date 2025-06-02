package org.example.doantn.Controller;

import org.example.doantn.Dto.request.AssignmentRequest;
import org.example.doantn.Dto.request.ClazzRequest;
import org.example.doantn.Dto.response.*;
import org.example.doantn.Entity.Clazz;
import org.example.doantn.Repository.CourseRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clazzes")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private SemesterRepo semesterRepo;

    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ClazzDTO>> getAllClazzes() {
        List<ClazzDTO> clazzDTOs = clazzService.getAllClazzes()
                .stream().map(ClazzDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clazzDTOs);
    }

    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping("/{maLop}/{hocki}")
    public ResponseEntity<ClazzDTO> getClazzByMaLopAndHocKi(@PathVariable String maLop,@PathVariable String hocki) {
        Clazz clazz = clazzService.getClazzByMaLopAndSemester(maLop,hocki);
        return ResponseEntity.ok(new ClazzDTO(clazz));
    }

    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping("/by-clazz/{maLop}/{hocki}")
    public ResponseEntity<List<TeacherDTO>> getTeachersByClazz(
            @PathVariable String maLop,
            @PathVariable String hocki
    ) {
        List<TeacherDTO> teachers = clazzService.getTeachersByClazz(maLop, hocki);
        if (teachers != null) {
            return ResponseEntity.ok(teachers);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // @PreAuthorize("hasRole('QLDT')")
    @GetMapping("/codes-by-class")
    public ResponseEntity<List<ClassTeacherDTO>> getTeacherCodesByClassDTOEndpoint() {
        List<ClassTeacherDTO> teacherCodesByClass = clazzService.getTeacherCodesByClassDTO();
        return ResponseEntity.ok(teacherCodesByClass);
    }

    // @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping("/unassigned")
    public ResponseEntity<List<ClazzDTO>> getUnassignedClazzes(
            @RequestParam(value = "ctdtCode", required = false) String ctdtCode,
            @RequestParam(value = "khoa", required = false) String khoa,
            @RequestParam(value = "hocKi", required = false) String hocKi) {
        List<ClazzDTO> unassignedClazzes = clazzService.getUnassignedClazzes(ctdtCode, khoa, hocKi);
        return ResponseEntity.ok(unassignedClazzes);
    }

    @GetMapping("/{maLop}/{hocki}/students")
    @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<StudentGradeDTO>> getStudentsByClazz(
            @PathVariable String maLop,
            @PathVariable String hocki) {
        List<StudentGradeDTO> studentGradeDTOs = clazzService.getStudentsByClazzAndSemester(maLop, hocki);
        if (studentGradeDTOs != null && !studentGradeDTOs.isEmpty()) {
            return ResponseEntity.ok(studentGradeDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addClazz(@RequestBody ClazzRequest request) {
        try {
            Clazz clazz = convertToEntity(request);
            Clazz savedClazz = clazzService.addClazz(clazz);
            return ResponseEntity.ok(convertToDTO(savedClazz));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }


    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ClazzDTO> updateClazz(@PathVariable Integer id,
                                                @RequestParam(required = false) String maLop,
                                                @RequestParam(required = false) String lichThi) {
        LocalDate examDate = (lichThi != null) ? LocalDate.parse(lichThi) : null;
        Clazz updatedClazz = clazzService.updateClazz(id, maLop, examDate);
        return ResponseEntity.ok(new ClazzDTO(updatedClazz));
    }


    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClazz(@PathVariable Integer id) {
        clazzService.deleteClazz(id);
        return ResponseEntity.ok("Xóa lớp học thành công!");
    }

    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping("/clazz/teacher-class-count")
    public ResponseEntity<Map<String, Integer>> getClazzCountPerTeacher() {
        Map<String, Integer> clazzCounts = clazzService.countClazzesPerTeacher();
        return ResponseEntity.ok(clazzCounts);
    }
    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")

    @PostMapping("/assign-teachers")
    public ResponseEntity<?> assignTeachersToClazzes(@RequestBody List<AssignmentRequest> assignmentRequests) {
        try {
            clazzService.assignTeachersToClazzes(assignmentRequests);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Phân công giáo viên thành công cho các lớp đã chọn");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi phân công giáo viên: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @PostMapping("/malop/{maLop}/semester/{hocKi}/assign-teacher/teacher/{maGv}")
    public ResponseEntity<?> assignTeacherToClazz(
            @PathVariable String maLop,
            @PathVariable String maGv,
            @PathVariable String hocKi) {
        try {
            clazzService.assignTeacherToClazz(maLop, maGv, hocKi);
            return ResponseEntity.ok("Phân công giáo viên thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi phân công giáo viên: " + e.getMessage());
        }
    }

    //@PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @GetMapping("/search/{ctdtCode}/{khoa}/{hocKi}")
    public ResponseEntity<List<ClazzDTO>> searchClazzesWithPathVariable(
            @PathVariable String ctdtCode,
            @PathVariable String khoa,
            @PathVariable String hocKi) {

        List<Clazz> clazzes = clazzService.findClazzesByCriteria(ctdtCode, khoa, hocKi);
        List<ClazzDTO> clazzDTOs = clazzes.stream()
                .map(ClazzDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clazzDTOs);
    }
    @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")
    @PostMapping("/generate-by-ctdt-khoa/{ctdtCode}/{khoa}/{hocKi}")
    public ResponseEntity<String> generateClazzesByCTDTCodeAndKhoa(
            @PathVariable String ctdtCode,
            @PathVariable String khoa,
            @PathVariable String hocKi
    ) {
        try {
            int numberOfClassesCreated = clazzService.generateClazzesForCTDTCodeAndKhoa(ctdtCode, khoa, hocKi);
            if (numberOfClassesCreated > 0) {
                return ResponseEntity.ok("Đã tạo " + numberOfClassesCreated + " lớp học thành công.");
            } else {
                return ResponseEntity.ok("Đã tạo lớp trước đó rồi.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
    @PreAuthorize("hasRole('QLDT') or hasRole('ADMIN')")

    @GetMapping("/malop/{maLop}/semester/{hocKi}/available-teachers")
    public ResponseEntity<List<TeacherDTO>> getAvailableTeachers(
            @PathVariable String maLop,
            @PathVariable String hocKi
    ) {
        try {
            List<TeacherDTO> availableTeachers = clazzService.getAvailableTeachersForClazz(maLop, hocKi);
            return ResponseEntity.ok(availableTeachers);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
          }
    }
    private Clazz convertToEntity(ClazzRequest request) {
        Clazz clazz = new Clazz();
        clazz.setLichThi(request.getLichThi());
        clazz.setMaLop(request.getMaLop());
        if (request.getMaHocPhan() != null) {
            courseRepo.findByMaHocPhan(request.getMaHocPhan()).ifPresent(clazz::setCourse);
        }
        if (request.getHocki() != null) {
            semesterRepo.findByName(request.getHocki()).ifPresent(clazz::setSemester);
        }
        return clazz;
    }

    private ClazzDTO convertToDTO(Clazz clazz) {
        return new ClazzDTO(clazz); // Sử dụng constructor ClazzDTO(Clazz clazz) đã được sửa
    }
}