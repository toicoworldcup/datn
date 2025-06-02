package org.example.doantn.Service;

import jakarta.transaction.Transactional;
import org.example.doantn.Dto.response.CthDTO;
import org.example.doantn.Dto.response.GraduationResultDTO;
import org.example.doantn.Entity.*;
import org.example.doantn.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {
    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private DangkihocphanRepo dangkihocphanRepo;

    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<CthDTO> getChuongTrinhDaoTaoVaDiem(String username) {
        Student student = studentRepo.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với username: " + username));

        Set<Course> chuongTrinhDaoTao = student.getCtdt().getCourses();
        String mssv = student.getMssv();

        // Lấy tất cả đăng ký học phần của sinh viên
        List<Dangkihocphan> dangkihocphans = dangkihocphanRepo.findByStudent_Mssv(mssv);

        // Tạo map từ maHocPhan đến Dangkihocphan, ưu tiên bản ghi mới nhất (có ID lớn hơn)
        Map<String, Dangkihocphan> diemSoMap = dangkihocphans.stream()
                .collect(Collectors.toMap(
                        dkhp -> dkhp.getCourse().getMaHocPhan(),
                        dkhp -> dkhp,
                        (existing, replacement) -> replacement // Lấy bản ghi sau nếu key trùng lặp
                ));

        List<CthDTO> result = new ArrayList<>();
        for (Course course : chuongTrinhDaoTao) {
            CthDTO dto = new CthDTO(
                    student.getCtdt().getName(),
                    course.getMaHocPhan(),
                    course.getName(),
                    course.getTinChi(),
                    null,
                    null
            );

            if (diemSoMap.containsKey(course.getMaHocPhan())) {
                Dangkihocphan dkhp = diemSoMap.get(course.getMaHocPhan());
                dto.setDiemGK(dkhp.getGki());
                dto.setDiemCK(dkhp.getCki());
            }
            result.add(dto);
        }
        return result;
    }

    public GraduationResultDTO xetTotNghiep(String mssv) {
        Student student = studentRepo.findByMssv(mssv)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với MSSV: " + mssv));
        List<Course> ctdtCourses = courseRepo.findByCtdts_MaCt(student.getCtdt().getMaCt());
        List<Dangkihocphan> allRegisteredCourses = dangkihocphanRepo.findByStudent_Mssv(mssv);

        // Lấy bản ghi đăng ký học phần mới nhất cho mỗi môn học
        Map<String, Dangkihocphan> latestRegisteredCourses = allRegisteredCourses.stream()
                .collect(Collectors.toMap(
                        dkhp -> dkhp.getCourse().getMaHocPhan(),
                        dkhp -> dkhp,
                        (existing, replacement) -> replacement.getId() > existing.getId() ? replacement : existing
                ));

        List<String> notCompletedCourses = new ArrayList<>();
        List<String> notGradedCourses = new ArrayList<>();
        List<String> failedCourses = new ArrayList<>();

        for (Course course : ctdtCourses) {
            if (!latestRegisteredCourses.containsKey(course.getMaHocPhan())) {
                notCompletedCourses.add(course.getName());
                continue;
            }
            Dangkihocphan dkhp = latestRegisteredCourses.get(course.getMaHocPhan());
            if (dkhp.getFinalGrade() == null) {
                notGradedCourses.add(course.getName());
            } else if (dkhp.getFinalGrade() < 4.0) {
                failedCourses.add(course.getName());
            }
        }

        if (!notCompletedCourses.isEmpty()) {
            return new GraduationResultDTO(false, "Chưa hoàn thành các môn học: " + String.join(", ", notCompletedCourses));
        }

        if (!notGradedCourses.isEmpty()) {
            return new GraduationResultDTO(false, "Chưa có điểm các môn học: " + String.join(", ", notGradedCourses));
        }

        if (!failedCourses.isEmpty()) {
            return new GraduationResultDTO(false, "Các môn học chưa đạt: " + String.join(", ", failedCourses));
        }

        return new GraduationResultDTO(true, "Đủ điều kiện tốt nghiệp.");
    }
    // Các phương thức khác của StudentService (getAllStudents, getStudentById, ...)
    @Autowired
    private BatchRepo batchRepo;
    @Autowired
    private DepartmentRepo departmentRepo;

    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }


    public Optional<Student> getStudentByMssv(String mssv) {
        return studentRepo.findByMssv(mssv);
    }


    public Student addStudent(Student student) {
        // Kiểm tra Batch ID
        if (student.getBatch() == null || student.getBatch().getId() == null) {
            throw new IllegalArgumentException(" Lỗi: Batch ID không được để trống!");
        }
        Batch batch = batchRepo.findById(student.getBatch().getId())
                .orElseThrow(() -> new IllegalArgumentException(" Lỗi: Batch ID " + student.getBatch().getId() + " không tồn tại!"));
        student.setBatch(batch);

        // Kiểm tra Department ID
        if (student.getDepartment() == null || student.getDepartment().getId() == null) {
            throw new IllegalArgumentException("Lỗi: Department ID không được để trống!");
        }
        Department department = departmentRepo.findById(student.getDepartment().getId())
                .orElseThrow(() -> new IllegalArgumentException(" Lỗi: Department ID " + student.getDepartment().getId() + " không tồn tại!"));
        student.setDepartment(department);

        return studentRepo.save(student);
    }

    public Optional<Student> getStudentByUsername(String username) {
        return studentRepo.findByUser_Username(username);
    }

    public List<Student> getStudentsByCtdtNameAndBatch(String ctdtName, String batchName) {
        return studentRepo.findByCtdtMaCtAndBatchName(ctdtName, batchName);
    }

    public List<Student> getStudentsByCtdtName(String maCt) {
        return studentRepo.findByCtdtMaCt(maCt);
    }

    public Student updateStudent(int id, Student updatedStudent) {
        Optional<Student> optionalStudent = studentRepo.findById(id);
        if (optionalStudent.isPresent()) {
            Student existingStudent = optionalStudent.get();
            existingStudent.setAddress(updatedStudent.getAddress());
            existingStudent.setDateOfBirth(updatedStudent.getDateOfBirth());
            existingStudent.setName(updatedStudent.getName());
            existingStudent.setEmail(updatedStudent.getEmail());
            existingStudent.setPhone(updatedStudent.getPhone());
            return studentRepo.save(existingStudent);
        } else {
            throw new RuntimeException("Không tìm thấy học viên với ID: " + id);
        }
    }

    public void deleteStudent(int id) {
        Optional<Student> optionalStudent = studentRepo.findById(id);
        if (optionalStudent.isPresent()) {
            studentRepo.deleteById(id);
        } else {
            throw new RuntimeException("Không tìm thấy học viên với ID: " + id);
        }
    }

    @Transactional
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null) {
            return false;
        }

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
            return true;
        }
        return false;
    }
}