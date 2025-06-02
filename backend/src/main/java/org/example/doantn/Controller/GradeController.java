package org.example.doantn.Controller;

import jakarta.validation.Valid;
import org.example.doantn.Dto.response.GradeDTO;
import org.example.doantn.Dto.request.GradeRequest;
import org.example.doantn.Entity.*;
import org.example.doantn.Repository.*;
import org.example.doantn.Service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@RestController
@RequestMapping("/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private ClazzRepo clazzRepo;
    @Autowired
    private SemesterRepo semesterRepo;
    @Autowired
    private DangkihocphanRepo dangkihocphanRepo;

    @GetMapping
    public ResponseEntity<List<GradeDTO>> getAllGrades() {
        List<GradeDTO> gradeDTOs = gradeService.getAllGradeRepos()
                .stream()
                .map(this::convertToDTO) // Sử dụng convertToDTO để bao gồm history
                .collect(Collectors.toList());

        return ResponseEntity.ok(gradeDTOs);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/me/semester/{semesterName}")
    public ResponseEntity<?> getMyGradesInSemester(
            Authentication authentication,
            @PathVariable String semesterName) {
        try {
            String username = authentication.getName();
            Student student = studentRepo.findByUser_Username(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với username: " + username));

            List<GradeDTO> gradeDTOs = gradeService.getGradesByStudentAndSemester(student.getMssv(), semesterName)
                    .stream()
                    .map(grade -> {
                        if (grade.getDiemGk() == -1 && grade.getDiemCk() == -1) {
                            return new GradeDTO(null, null,
                                    grade.getClazz() != null ? grade.getClazz().getMaLop() : null,
                                    grade.getSemester() != null ? grade.getSemester().getName() : null,
                                    grade.getStudent() != null ? grade.getStudent().getMssv() : null,
                                    grade.getHistory()); // Bao gồm history
                        }
                        return convertToDTO(grade); // Sử dụng convertToDTO để bao gồm history
                    })
                    .collect(Collectors.toList());

            if (gradeDTOs.isEmpty()) {
                return ResponseEntity.ok("Chưa có điểm cho kỳ này.");
            }

            return ResponseEntity.ok(gradeDTOs);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin.");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{semester}")
    public ResponseEntity<List<GradeDTO>> getStudentGrades1(
            Authentication authentication,
            @PathVariable String semester) {

        String username = authentication.getName();
        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với username: " + username));

        List<GradeDTO> gradeDTOs = gradeService.getGradeByMssvAndSemester(student.getMssv(), semester)
                .stream()
                .map(this::convertToDTO) // Sử dụng convertToDTO để bao gồm history
                .collect(Collectors.toList());

        return ResponseEntity.ok(gradeDTOs);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/mssv/{mssv}/hocki/{semester}")
    public ResponseEntity<List<GradeDTO>> getStudentGrades2(
            @PathVariable String mssv,
            @PathVariable String semester) {

        Student student = studentRepo.findByMssv(mssv)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với mssv: " + mssv));

        List<GradeDTO> gradeDTOs = gradeService.getGradeByMssvAndSemester(student.getMssv(), semester)
                .stream()
                .map(this::convertToDTO) // Sử dụng convertToDTO để bao gồm history
                .collect(Collectors.toList());

        return ResponseEntity.ok(gradeDTOs);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping
    public ResponseEntity<?> addGrade(
            @RequestBody @Valid GradeRequest request,
            Authentication authentication) {

        try {
            Grade grade = convertToEntity(request, authentication.getName());
            Grade savedGrade = gradeService.addGrade(grade);
            updateStudentCourseGrade(savedGrade); // Pass savedGrade
            return ResponseEntity.ok(convertToDTO(savedGrade));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void updateStudentCourseGrade(Grade grade) {
        if (grade == null || grade.getStudent() == null || grade.getClazz() == null || grade.getClazz().getCourse() == null) {
            return;
        }

        Student student = grade.getStudent();
        Course course = grade.getClazz().getCourse();
        Semester semester = grade.getSemester(); // Lấy thông tin học kỳ

        try {
            // Tìm Dangkihocphan dựa trên Student, Course và Semester
            Optional<Dangkihocphan> existingEnrollment = dangkihocphanRepo.findByStudent_MssvAndCourse_MaHocPhanAndSemester_Name(student.getMssv(), course.getMaHocPhan(), semester.getName()).stream().findFirst();

            Dangkihocphan enrollment;
            if (existingEnrollment.isPresent()) {
                // Nếu đã tồn tại, lấy ra để cập nhật
                enrollment = existingEnrollment.get();
            } else {
                // Nếu chưa tồn tại, tạo mới
                enrollment = new Dangkihocphan(student, course);
                enrollment.setSemester(semester); // Set học kỳ cho enrollment mới
            }

            // Cập nhật điểm
            if (grade.getDiemGk() != null && grade.getDiemGk() != -1) {
                enrollment.setGki(grade.getDiemGk());
            }
            if (grade.getDiemCk() != null && grade.getDiemCk() != -1) {
                enrollment.setCki(grade.getDiemCk());
            }

            // Tính toán điểm cuối kỳ và xếp loại
            gradeService.calculateFinalGradeAndLetter(enrollment);

            // Lưu lại thông tin
            dangkihocphanRepo.save(enrollment);

        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật điểm và tính toán kết quả học phần: " + e.getMessage());
        }
    }

    @PutMapping("/{mssv}/{maLop}/{semesterName}")
    public ResponseEntity<?> updateGrade(
            @PathVariable String mssv,
            @PathVariable String maLop,
            @PathVariable String semesterName,
            @RequestBody GradeRequest updatedGradeRequest) {
        try {
            Grade updatedGradeEntity = convertToEntity(updatedGradeRequest, null); // null vì không cần username ở đây
            Grade savedGrade = gradeService.updateGrade(mssv, maLop, semesterName, updatedGradeEntity);
            updateStudentCourseGrade(savedGrade); // Thêm dòng này
            return ResponseEntity.ok(convertToDTO(savedGrade));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // Trả về 404 nếu không tìm thấy
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable int id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }

    private Grade convertToEntity(GradeRequest request, String username) {
        Semester semester = semesterRepo.findByName(request.getSemesterName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học kỳ với tên: " + request.getSemesterName()));

        Clazz clazz = clazzRepo.findByMaLopAndSemester_Name(request.getMaLop(), request.getSemesterName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp  " + request.getMaLop() + request.getSemesterName()));

        Student student = studentRepo.findByMssv(request.getMssv())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với mssv: " + request.getMssv()));


        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setClazz(clazz);
        grade.setSemester(semester);
        grade.setDiemGk(request.getGki());
        grade.setDiemCk(request.getCki());
        return grade;
    }

    private GradeDTO convertToDTO(Grade grade) {
        return new GradeDTO(
                grade.getDiemCk(),
                grade.getDiemGk(),
                grade.getClazz() != null ? grade.getClazz().getMaLop() : null,
                grade.getSemester() != null ? grade.getSemester().getName() : null,
                grade.getStudent() != null ? grade.getStudent().getMssv() : null,
                grade.getHistory() // Thêm history vào DTO
        );
    }
}