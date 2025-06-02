package org.example.doantn.Controller;

import org.example.doantn.Dto.request.CtdtRequest;
import org.example.doantn.Dto.request.CthRequest;
import org.example.doantn.Dto.response.CtdtDTO;
import org.example.doantn.Dto.response.CthDTO;
import org.example.doantn.Entity.Batch;
import org.example.doantn.Entity.Ctdt;
import org.example.doantn.Service.CtdtService;
import org.example.doantn.Repository.BatchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Ctdt")
public class CtdtController {
    @Autowired
    private CtdtService ctdtService;
    @Autowired
    private BatchRepo batchRepo;

    //@PreAuthorize("hasRole('QLDT')")
    @GetMapping
    public ResponseEntity<List<CtdtDTO>> getAllCtdts() {
        List<CtdtDTO> ctdtDTOS = ctdtService.getAllCtdts()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ctdtDTOS);
    }

    //@PreAuthorize("hasRole('QLDT')")
    @GetMapping("/search/{name}")
    public ResponseEntity<CtdtDTO> searchCtdtByMaCt(@PathVariable String name) {
        Optional<Ctdt> ctdt = ctdtService.getCtdtByMaCt(name);

        return ctdt.map(value -> ResponseEntity.ok(convertToDTO(value))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    //@PreAuthorize("hasRole('QLDT')")
    @PostMapping
    public ResponseEntity<?> addCtdt(@RequestBody CtdtRequest request) {
        try {
            Ctdt ctdt = convertToEntity(request);
            Ctdt savedCtdt = ctdtService.createCtdt(ctdt);
            return ResponseEntity.ok(convertToDTO(savedCtdt));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    //  @PreAuthorize("hasRole('QLDT')")
    @GetMapping("/{maCt}/{khoa}")
    public List<CthDTO> getCoursesByMaCtAndKhoa(@PathVariable String maCt, @PathVariable String khoa) {
        return ctdtService.getCoursesByMaCtAndKhoa(maCt, khoa);
    }

    @PreAuthorize("hasRole('QLDT')")
    @PostMapping("/import-courses/{maCt}/{khoa}")
    public ResponseEntity<?> importCoursesFromExcel(
            @PathVariable String maCt,
            @PathVariable String khoa,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng chọn file Excel để tải lên.");
        }

        try {
            List<String> maHocPhanList = ctdtService.readMaHocPhanFromExcel(file);
            ctdtService.assignCourses(maCt, khoa, maHocPhanList);
            return ResponseEntity.ok("Đã nhập thành công " + maHocPhanList.size() + " học phần vào CTĐT có mã: " + maCt + " cho khóa " + khoa);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi đọc file Excel: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //@PreAuthorize("hasRole('QLDT')")
    @PostMapping("/ctdt/assign/{khoa}")
    public ResponseEntity<?> addCourses(
            @PathVariable String khoa,
            @RequestBody CthRequest request) {
        try {
            ctdtService.assignCourses(request.getMaCt(), khoa, request.getMaHocPhanList());
            List<CthDTO> cthDTOList = ctdtService.getCoursesByMaCtAndKhoa(request.getMaCt(), khoa);
            return ResponseEntity.ok(cthDTOList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi thêm học phần: " + e.getMessage());
        }
    }

    private Ctdt convertToEntity(CtdtRequest request) {
        Ctdt ctdt = new Ctdt();
        ctdt.setName(request.getName());
        ctdt.setMaCt(request.getMaCt());
        Batch batch = batchRepo.findByName(request.getKhoa())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khoá với tên: " + request.getKhoa()));
        ctdt.setBatch(batch);
        return ctdt;
    }

    private CtdtDTO convertToDTO(Ctdt ctdt) {
        return new CtdtDTO(ctdt);
    }
}