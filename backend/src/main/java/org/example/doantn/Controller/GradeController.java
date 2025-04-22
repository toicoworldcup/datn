package org.example.doantn.Controller;

import jakarta.validation.Valid;
import org.example.doantn.Dto.request.DkhpRequest;
import org.example.doantn.Dto.response.DkhpDTO;
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

@RestController
@RequestMapping("/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private TeacherRepo teacherRepo;
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
                .map(GradeDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(gradeDTOs);
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
                .map(GradeDTO::new)
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
                .map(GradeDTO::new)
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
            updateStudentCourseGrade(grade);
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

        try {
            Dangkihocphan enrollment = dangkihocphanRepo.findByStudent_MssvAndMaHocPhan(
                    student.getMssv(), course.getMaHocPhan()
            ).stream().findFirst().orElse(new Dangkihocphan(student, course));

            if (grade.getDiemGk() != -1) {
                enrollment.setGki(grade.getDiemGk());
            }
            if (grade.getDiemCk() != -1) {
                enrollment.setCki(grade.getDiemCk());
            }

            dangkihocphanRepo.save(enrollment);
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật điểm cho học phần: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGrade(@PathVariable int id, @RequestBody Grade updatedGrade) {
        try {
            Grade savedGrade = gradeService.updateGrade(id, updatedGrade);
            return ResponseEntity.ok(new GradeDTO(savedGrade));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable int id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
    private Grade convertToEntity(GradeRequest request, String username) {
        Teacher teacher = teacherRepo.findByUser_Username(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giáo viên với username: " + username));

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
        return gradeService.addGrade(grade);
    }

    private GradeDTO convertToDTO(Grade grade) {
        return new GradeDTO(
                grade.getDiemCk(),
                grade.getDiemGk(),
                grade.getClazz() != null ? grade.getClazz().getMaLop() : null,
                grade.getSemester() != null ? grade.getSemester().getName() : null,
                grade.getStudent() != null ? grade.getStudent().getMssv() : null


                );
    }
}
