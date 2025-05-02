package org.example.doantn.Controller;

import org.example.doantn.Dto.request.CtdtRequest;
import org.example.doantn.Dto.request.CthRequest;
import org.example.doantn.Dto.request.StudentRequest;
import org.example.doantn.Dto.response.CtdtDTO;
import org.example.doantn.Dto.response.CthDTO;
import org.example.doantn.Dto.response.StudentDTO;
import org.example.doantn.Entity.*;
import org.example.doantn.Repository.BatchRepo;
import org.example.doantn.Service.CtdtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Ctdt")
public class CtdtController {
    @Autowired
    private CtdtService ctdtService;
    @Autowired
    private BatchRepo batchRepo;

    @PreAuthorize("hasRole('QLDT')")
    @GetMapping
    public ResponseEntity<List<CtdtDTO>> getAllCtdts() {
        List<CtdtDTO> ctdtDTOS = ctdtService.getAllCtdts()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ctdtDTOS);
    }

    @PreAuthorize("hasRole('QLDT')")
    @GetMapping("/search/{name}")
    public ResponseEntity<CtdtDTO> searchCtdtByMaCt(@PathVariable String name) {
        Optional<Ctdt> ctdt = ctdtService.getCtdtByMaCt(name);

        if (!ctdt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(convertToDTO(ctdt.get()));
    }

    @PreAuthorize("hasRole('QLDT')")
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
    @GetMapping("/{maCt}")
    public List<CthDTO> getCoursesByMaCt(@PathVariable String maCt) {
        return ctdtService.getCoursesByMaCt(maCt);
    }

    @PreAuthorize("hasRole('QLDT')")
    @PostMapping("/ctdt/assign")
    public ResponseEntity<?> addCourses(@RequestBody CthRequest request) {
        try {
            // Thêm nhiều học phần vào chương trình đào tạo
            ctdtService.assignCourses(request.getMaCt(), request.getMaHocPhanList());

            // Trả về danh sách học phần đã được cập nhật
            List<CthDTO> cthDTOList = ctdtService.getCoursesByMaCt(request.getMaCt());
            return ResponseEntity.ok(cthDTOList);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi thêm học phần: " + e.getMessage());
        }
    }



    private Ctdt convertToEntity(CtdtRequest request) {
        Ctdt ctdt = new Ctdt();
        ctdt.setName(request.getName());
        Batch batch = batchRepo.findByName(request.getKhoa())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khoá với tên: " + request.getKhoa()));
        return ctdt;
    }

    private CtdtDTO convertToDTO(Ctdt ctdt) {
        return new CtdtDTO(
                ctdt.getName()
                );
    }
}
