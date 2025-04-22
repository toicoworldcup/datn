package org.example.doantn.Controller;

import org.example.doantn.Dto.request.ClazzRequest;
import org.example.doantn.Dto.request.TeacherRequest;
import org.example.doantn.Dto.response.ClazzDTO;
import org.example.doantn.Dto.response.TeacherDTO;
import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Teacher;
import org.example.doantn.Repository.CourseRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    //@PreAuthorize("hasRole('QLDT')")
    @GetMapping
    public ResponseEntity<List<ClazzDTO>> getAllClazzes() {
        List<ClazzDTO> clazzDTOs = clazzService.getAllClazzes()
                .stream().map(ClazzDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clazzDTOs);
    }

    //@PreAuthorize("hasRole('QLDT')")
    @GetMapping("/{maLop}/{hocki}")
    public ResponseEntity<ClazzDTO> getClazzByMaLopAndHocKi(@PathVariable String maLop,@PathVariable String hocki) {
        Clazz clazz = clazzService.getClazzByMaLopAndSemester(maLop,hocki);
        return ResponseEntity.ok(new ClazzDTO(clazz));
    }


    //@PreAuthorize("hasRole('QLDT')")
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


   // @PreAuthorize("hasRole('QLDT')")
    @PutMapping("/{id}")
    public ResponseEntity<ClazzDTO> updateClazz(@PathVariable Integer id,
                                                @RequestParam(required = false) String maLop,
                                                @RequestParam(required = false) String lichThi) {
        LocalDate examDate = (lichThi != null) ? LocalDate.parse(lichThi) : null;
        Clazz updatedClazz = clazzService.updateClazz(id, maLop, examDate);
        return ResponseEntity.ok(new ClazzDTO(updatedClazz));
    }


   // @PreAuthorize("hasRole('QLDT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClazz(@PathVariable Integer id) {
        clazzService.deleteClazz(id);
        return ResponseEntity.ok("Xóa lớp học thành công!");
    }

    //@PreAuthorize("hasRole('QLDT')")
    @GetMapping("/clazz/teacher-class-count")
    public ResponseEntity<Map<String, Integer>> getClazzCountPerTeacher() {
        Map<String, Integer> clazzCounts = clazzService.countClazzesPerTeacher();
        return ResponseEntity.ok(clazzCounts);
    }

   // @PreAuthorize("hasRole('QLDT')")
    @PostMapping("/malop/{maLop}/semester/{hocKi}/assign-teacher/teacher/{maGv}")
    public ResponseEntity<?> assignTeacherToClazz(
            @PathVariable String maLop,
            @PathVariable String maGv,
            @PathVariable String hocKi) {
        try {

            clazzService.assignTeacherToClazz(maLop, maGv, hocKi);
            return ResponseEntity.ok("Phân công giáo viên thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi phân công giáo viên: " + e.getMessage());
        }
    }
    //@PreAuthorize("hasRole('QLDT')")
    @PostMapping("/generate")
    public ResponseEntity<String> generateClazzes(
            @RequestParam String maHocPhan,
            @RequestParam String hocKi
    ) {
        try {
            clazzService.generateClazzForCourse(maHocPhan, hocKi);
            return ResponseEntity.ok("Tạo lớp thành công cho học phần: " + maHocPhan + " - Học kỳ: " + hocKi);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    private Clazz convertToEntity(ClazzRequest request) {
        Clazz clazz = new Clazz();
        clazz.setLichThi(request.getLichThi());
        clazz.setMaLop(request.getMaLop());
        if(request.getMaHocPhan() != null){
            courseRepo.findByMaHocPhan(request.getMaHocPhan()).ifPresent(clazz::setCourse);
        }
        if(request.getHocki() != null){
            semesterRepo.findByName(request.getHocki()).ifPresent(clazz::setSemester);
        }


        return clazz;
    }
    private ClazzDTO convertToDTO(Clazz clazz) {
        return new ClazzDTO(
                clazz.getMaLop(),
                clazz.getSemester() != null ? clazz.getSemester().getName() : null,
                clazz.getCourse() != null ? clazz.getCourse().getMaHocPhan() : null,
                clazz.getLichThi()
                );
    }
}
