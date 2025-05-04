package org.example.doantn.Controller;

import org.example.doantn.Dto.request.StudentRequest;
import org.example.doantn.Dto.request.TeacherRequest;
import org.example.doantn.Dto.response.ClazzDTO;
import org.example.doantn.Dto.response.TeacherDTO;
import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Student;
import org.example.doantn.Entity.Teacher;
import org.example.doantn.Repository.DepartmentRepo;
import org.example.doantn.Repository.TeacherRepo;
import org.example.doantn.Service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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

    @PreAuthorize("hasRole('ADMIN')")
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

        if (teacher.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(convertToDTO(teacher.get()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{maGv}")
    public ResponseEntity<TeacherDTO> getTeacherByMaGvForAdmin(@PathVariable String maGv) {
        Optional<Teacher> teacher = teacherRepo.findByMaGv(maGv);

        if (teacher.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(convertToDTO(teacher.get()));
    }


    @PreAuthorize("hasRole('ADMIN')")
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

    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TeacherDTO> updateTeacher(@PathVariable int id, @RequestBody Teacher updatedTeacher) {
        Teacher teacher = teacherService.updateTeacher(id, updatedTeacher);
        return ResponseEntity.ok(convertToDTO(teacher));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeacher(@PathVariable int id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok("Xóa giáo viên thành công!");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{teacherId}/class-count")
    public ResponseEntity<Integer> getClassCount(@PathVariable int teacherId) {
        int count = teacherService.getNumberOfClassesByTeacher(teacherId);
        return ResponseEntity.ok(count);
    }
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/{maGv}/clazzes")
    public ResponseEntity<Set<ClazzDTO>> getClazzesByTeacher(
            @PathVariable String maGv, Authentication authentication) {

        String username = authentication.getName();
        Optional<Teacher> teacher = teacherService.getTeacherByUsername(username);
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            Set<Clazz> clazzes = teacherService.getClazzesByMaGv(maGv);
            Set<ClazzDTO> clazzDTOs = clazzes.stream()
                    .map(ClazzDTO::new)
                    .collect(Collectors.toSet());
            return ResponseEntity.ok(clazzDTOs);
        }

        if (teacher.isEmpty() || !teacher.get().getMaGv().equals(maGv)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Set<Clazz> clazzes = teacherService.getClazzesByMaGv(maGv);
        Set<ClazzDTO> clazzDTOs = clazzes.stream()
                .map(ClazzDTO::new)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(clazzDTOs);
    }


    private TeacherDTO convertToDTO(Teacher teacher) {
        return new TeacherDTO(
                teacher.getAddress(),
                teacher.getCccd(),
                teacher.getDateOfBirth(),
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
        if (request.getDepartmentName() != null) {
            departmentRepo.findByName(request.getDepartmentName()).ifPresent(teacher::setDepartment);
        }
        return teacher;
    }

}
