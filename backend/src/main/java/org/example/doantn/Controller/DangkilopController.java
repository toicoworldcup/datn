package org.example.doantn.Controller;

import org.example.doantn.Dto.request.DkhpRequest;
import org.example.doantn.Dto.request.DklRequest;
import org.example.doantn.Dto.response.DkhpDTO;
import org.example.doantn.Dto.response.DklDTO;
import org.example.doantn.Entity.*;
import org.example.doantn.Repository.ClazzRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Repository.StudentRepo;
import org.example.doantn.Service.DangkilopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dangkilop")
public class DangkilopController {
    @Autowired
    private DangkilopService dangkilopService;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private ClazzRepo clazzRepo;
    @Autowired
    private SemesterRepo semesterRepo;

    //tim theo mssv
    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping
    public ResponseEntity<List<DklDTO>> getAllDangkilopByMSSV(
            Authentication authentication,
            @RequestParam String semester) {
        String username = authentication.getName();
        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với username: " + username));

        List<Dangkilop> registrations = dangkilopService.getDangkilopByMssvAndSemester(student.getMssv(), semester);
        List<DklDTO> dtoList = registrations.stream().map(DklDTO::new).toList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DklDTO> getDangkilopById(@PathVariable Integer id) {
        Dangkilop dangkilop = dangkilopService.getDangkilopById(id);
        return ResponseEntity.ok(new DklDTO(dangkilop));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/register")
    public ResponseEntity<?> registerClass(
            Authentication authentication,
            @RequestBody DklRequest request) {

        try {
            Dangkilop dangkilop = convertToEntity(request, authentication.getName());
            Dangkilop savedDkl = dangkilopService.registerClass(dangkilop);
            return ResponseEntity.ok(convertToDTO(savedDkl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }


    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDangkilop(@PathVariable Integer id) {
        dangkilopService.deleteDangkilop(id);
        return ResponseEntity.noContent().build();
    }
    private Dangkilop convertToEntity(DklRequest request, String username) {
        Dangkilop dangkilop = new Dangkilop();

        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với username: " + username));

        Clazz clazz = clazzRepo.findByMaLopAndSemester_Name(request.getMaLop(), request.getSemesterName())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp  " + request.getMaLop() + request.getSemesterName()));

        Semester semester = semesterRepo.findByName(request.getSemesterName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học kỳ với tên: " + request.getSemesterName()));
        dangkilop.setStudent(student);
        dangkilop.setClazz(clazz);
        dangkilop.setSemester(semester);

        return dangkilop;
    }
    private DklDTO convertToDTO(Dangkilop dangkilop) {
        return new DklDTO(
                dangkilop.getClazz() != null ? dangkilop.getClazz().getMaLop() : null,
                dangkilop.getSemester() != null ? dangkilop.getSemester().getName() : null
        );
    }
}
