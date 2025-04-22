package org.example.doantn.Controller;

import org.example.doantn.Dto.request.ClazzRequest;
import org.example.doantn.Dto.request.CourseRequest;
import org.example.doantn.Dto.response.ClazzDTO;
import org.example.doantn.Dto.response.CourseDTO;
import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Course;
import org.example.doantn.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    //@PreAuthorize("hasRole('QLDT')")
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courseDTOs = courseService.getAllCourses()
                .stream()
                .map(CourseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courseDTOs);
    }

   // @PreAuthorize("hasRole('QLDT')")
    @GetMapping("/{maHocPhan}")
    public ResponseEntity<CourseDTO> getCourseByMaHocPhan(@PathVariable String maHocPhan) {
        try {
            Course course = courseService.getCourseByMaHocPhan(maHocPhan);
            return ResponseEntity.ok(new CourseDTO(course));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //@PreAuthorize("hasRole('QLDT')")
    @GetMapping("/clazzes/{maHocPhan}-{semesterName}")
    public ResponseEntity<Set<ClazzDTO>> getClazzesByMaHocPhan(@PathVariable String maHocPhan,@PathVariable String semesterName) {
        Set<ClazzDTO> clazzDTOs = courseService.getClazzesByMaHocPhan(maHocPhan,semesterName)
                .stream()
                .map(ClazzDTO::new)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(clazzDTOs);
    }

    //@PreAuthorize("hasRole('QLDT')")
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CourseRequest request) {
        try {
            Course course = convertToEntity(request);
            Course savedCourse = courseService.createCourse(course);
            return ResponseEntity.ok(convertToDTO(savedCourse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }


    }

   //@PreAuthorize("hasRole('QLDT')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Integer id, @RequestBody CourseDTO courseDTO) {
        try {
            Course updatedCourse = courseService.updateCourse(id,
                    courseDTO.getTenMonHoc(),
                    courseDTO.getMaHocPhan(),
                    courseDTO.getSoTinChi());
            return ResponseEntity.ok(new CourseDTO(updatedCourse));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //@PreAuthorize("hasRole('QLDT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    private Course convertToEntity(CourseRequest request) {
        Course course = new Course();
        course.setMaHocPhan(request.getMaHocPhan());
        course.setName(request.getTenMonHoc());
        course.setTinChi(request.getSoTinChi());


        return course;
    }
    private CourseDTO convertToDTO(Course course) {
        return new CourseDTO(
                course.getMaHocPhan(),
                course.getTinChi(),
                course.getName()
        );
    }
}
