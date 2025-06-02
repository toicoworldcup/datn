package org.example.doantn.Controller;

import org.example.doantn.Dto.request.DklRequest;
import org.example.doantn.Dto.request.UpdateRequestList;
import org.example.doantn.Dto.response.DklDTO;
import org.example.doantn.Dto.response.SpecialClassRequestDTO;
import org.example.doantn.Entity.*;
import org.example.doantn.Repository.*;
import org.example.doantn.Service.DangkilopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private DangkihocphanRepo dangkihocphanRepo;
    @Autowired
    private SpecialClassRequestRepo specialClassRequestRepo;

    // Lấy danh sách đăng ký lớp của sinh viên theo học kỳ
    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/hocki/{semester}")
    public ResponseEntity<List<DklDTO>> getAllDangkilopByMSSV(
            Authentication authentication,
            @PathVariable String semester) {
        String username = authentication.getName();
        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với username: " + username));

        List<Dangkilop> registrations = dangkilopService.getDangkilopByMssvAndSemester(student.getMssv(), semester);
        List<DklDTO> dtoList = registrations.stream().map(DklDTO::new).toList();

        return ResponseEntity.ok(dtoList);
    }

    // Lấy thông tin đăng ký lớp theo ID
    @GetMapping("/{id}")
    public ResponseEntity<DklDTO> getDangkilopById(@PathVariable Integer id) {
        Dangkilop dangkilop = dangkilopService.getDangkilopById(id);
        return ResponseEntity.ok(new DklDTO(dangkilop));
    }

    // Sinh viên đăng ký lớp
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/register")
    public ResponseEntity<?> registerClass(
            Authentication authentication,
            @RequestBody DklRequest request) {

        try {
            String username = authentication.getName();
            Student student = studentRepo.findByUser_Username(username)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với username: " + username));

            Semester semester = semesterRepo.findByName(request.getSemesterName())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học kỳ với tên: " + request.getSemesterName()));

            Clazz clazzToRegister = clazzRepo.findByMaLopAndSemester_Name(request.getMaLop(), request.getSemesterName())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp " + request.getMaLop() + " trong học kỳ " + request.getSemesterName()));

            // Lấy danh sách học phần mà sinh viên đã đăng ký trong học kỳ này
            List<Dangkihocphan> registeredDkhp = dangkihocphanRepo.findByStudent_MssvAndSemesterName(student.getMssv(), request.getSemesterName());
            List<String> registeredCourseCodes = registeredDkhp.stream()
                    .map(dkhp -> dkhp.getCourse().getMaHocPhan())
                    .toList();

            // Kiểm tra xem lớp thuộc học phần nào
            if (clazzToRegister.getCourse() == null || !registeredCourseCodes.contains(clazzToRegister.getCourse().getMaHocPhan())) {
                // Học phần chưa được đăng ký, tạo yêu cầu đặc biệt
                // Kiểm tra xung đột lịch trước khi tạo yêu cầu đặc biệt
                if (dangkilopService.isScheduleConflict(student.getMssv(), clazzToRegister.getMaLop(), semester.getName())) {
                    return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                        put("message", "Lớp học này bị trùng lịch với thời khóa biểu của bạn.");
                    }});
                }

                SpecialClassRequest specialRequest = new SpecialClassRequest();
                specialRequest.setStudent(student);
                specialRequest.setClazz(clazzToRegister);
                specialRequest.setSemester(semester);
                specialRequest.setRequestDate(LocalDate.now().toString());
                specialRequest.setStatus("PENDING"); // Trạng thái mặc định là chờ duyệt
                specialClassRequestRepo.save(specialRequest);

                return ResponseEntity.ok(new HashMap<String, String>() {{
                    put("message", "Yêu cầu đăng ký lớp cho học phần chưa đăng ký đã được gửi đến QLĐT.");
                }});
            }

            // Học phần đã được đăng ký, tiến hành đăng ký lớp bình thường
            Dangkilop dangkilop = convertToEntity(request, username);
            // Kiểm tra xem sinh viên đã đăng ký lớp này trong học kỳ này chưa
            List<Dangkilop> existingRegistrations = dangkilopService.getDangkilopByMssvAndSemester(student.getMssv(), request.getSemesterName());
            boolean alreadyRegistered = existingRegistrations.stream()
                    .anyMatch(dkl -> dkl.getClazz().getMaLop().equals(request.getMaLop()));

            if (alreadyRegistered) {
                return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                    put("message", "Bạn đã đăng ký lớp " + request.getMaLop() + " trong học kỳ " + request.getSemesterName() + " rồi.");
                }});
            }

            Dangkilop savedDkl = dangkilopService.registerClass(dangkilop);
            return ResponseEntity.ok(convertToDTO(savedDkl));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("message", e.getMessage());
            }});
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("message", e.getMessage());
            }});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new HashMap<String, String>() {{
                put("message", "Lỗi hệ thống: " + e.getMessage());
            }});
        }
    }

    // Xóa đăng ký lớp của sinh viên
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
                dangkilop.getId(), // Thêm ID vào DTO
                dangkilop.getClazz() != null ? dangkilop.getClazz().getMaLop() : null,
                dangkilop.getSemester() != null ? dangkilop.getSemester().getName() : null,
                dangkilop.getStudent() != null ? dangkilop.getStudent().getMssv() : null
        );
    }

    // API cho QLĐT quản lý yêu cầu đặc biệt

    // Lấy danh sách các yêu cầu đang chờ duyệt (sử dụng DTO)
    @PreAuthorize("hasRole('QLDT')")
    @GetMapping("/special-requests/pending")
    public ResponseEntity<List<SpecialClassRequestDTO>> getPendingSpecialRequests() {
        List<SpecialClassRequest> pendingRequests = specialClassRequestRepo.findByStatus("PENDING");
        List<SpecialClassRequestDTO> dtoList = pendingRequests.stream()
                .map(SpecialClassRequestDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    // Phê duyệt yêu cầu đặc biệt
    @PreAuthorize("hasRole('QLDT')")
    @PostMapping("/special-requests/{requestId}/approve")
    public ResponseEntity<String> approveSpecialRequest(@PathVariable Integer requestId) {
        return updateSpecialRequestStatus(requestId, "APPROVED");
    }

    // Từ chối yêu cầu đặc biệt
    @PreAuthorize("hasRole('QLDT')")
    @PostMapping("/special-requests/{requestId}/reject")
    public ResponseEntity<String> rejectSpecialRequest(@PathVariable Integer requestId) {
        return updateSpecialRequestStatus(requestId, "REJECTED");
    }
    @PreAuthorize("hasRole('QLDT')")
    @PostMapping("/special-requests/approve-multiple")
    public ResponseEntity<String> approveMultipleSpecialRequests(@RequestBody UpdateRequestList requestList) {
        if (requestList == null || requestList.getRequestIds() == null || requestList.getRequestIds().isEmpty()) {
            return ResponseEntity.badRequest().body("Danh sách ID yêu cầu không được rỗng.");
        }

        for (Integer requestId : requestList.getRequestIds()) {
            try {
                updateSpecialRequestStatus(requestId, "APPROVED");
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy yêu cầu với ID: " + requestId);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi phê duyệt yêu cầu ID " + requestId + ": " + e.getMessage());
            }
        }
        return ResponseEntity.ok("Đã phê duyệt thành công các yêu cầu đã chọn.");
    }

    // Xóa nhiều đăng ký lớp của sinh viên cùng lúc
    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<?> bulkDeleteDangkilop(
            Authentication authentication,
            @RequestBody List<Integer> registrationIds) {
        try {
            String username = authentication.getName();
            Student student = studentRepo.findByUser_Username(username)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với username: " + username));

            dangkilopService.bulkDeleteDangkilop(registrationIds, student.getMssv());
            return ResponseEntity.ok("Đã xóa thành công các lớp đã chọn.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa các lớp đã chọn: " + e.getMessage());
        }
    }
    @PreAuthorize("hasRole('QLDT')")
    @PostMapping("/special-requests/reject-multiple")
    public ResponseEntity<String> rejectMultipleSpecialRequests(@RequestBody UpdateRequestList requestList) {
        if (requestList == null || requestList.getRequestIds() == null || requestList.getRequestIds().isEmpty()) {
            return ResponseEntity.badRequest().body("Danh sách ID yêu cầu không được rỗng.");
        }

        for (Integer requestId : requestList.getRequestIds()) {
            try {
                updateSpecialRequestStatus(requestId, "REJECTED");
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy yêu cầu với ID: " + requestId);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi từ chối yêu cầu ID " + requestId + ": " + e.getMessage());
            }
        }
        return ResponseEntity.ok("Đã từ chối thành công các yêu cầu đã chọn.");
    }

    private ResponseEntity<String> updateSpecialRequestStatus(Integer requestId, String status) {
        SpecialClassRequest request = specialClassRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu với ID: " + requestId));
        request.setStatus(status);
        specialClassRequestRepo.save(request);

        // Nếu được duyệt, tạo bản ghi Dangkilop và tăng số lượng sinh viên
        if (status.equals("APPROVED")) {
            Dangkilop dkl = new Dangkilop();
            dkl.setStudent(request.getStudent());
            dkl.setClazz(request.getClazz());
            dkl.setSemester(request.getSemester());
            try {
                dangkilopService.registerClass(dkl); // Sử dụng service để đảm bảo logic nghiệp vụ và tăng số lượng sinh viên
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body("Không thể duyệt yêu cầu: " + e.getMessage());
            }
        }

        return ResponseEntity.ok("Đã cập nhật trạng thái yêu cầu thành: " + status);
    }
}