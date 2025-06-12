package org.example.doantn.Service;

import jakarta.transaction.Transactional;
import org.example.doantn.Entity.*;
import org.example.doantn.Import.TeacherImport;
import org.example.doantn.Repository.DepartmentRepo;
import org.example.doantn.Repository.TeacherRepo;
import org.example.doantn.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepo teacherRepo;
    @Autowired
    private DepartmentRepo departmentRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TeacherImport teacherImport;

    public List<Teacher> getAllTeachers() {
        return teacherRepo.findAll();
    }

    public Teacher addTeacher(Teacher teacher) {
        if (teacher.getDepartment() != null && teacher.getDepartment().getId() != 0) {
            teacher.setDepartment(departmentRepo.findById(teacher.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found")));
        } else {
            teacher.setDepartment(null);
        }
        return teacherRepo.save(teacher);
    }

    public Teacher updateTeacher(int id, Teacher updatedTeacherData, String departmentName) {
        Optional<Teacher> optionalTeacher = teacherRepo.findById(id);
        if (optionalTeacher.isPresent()) {
            Teacher existingTeacher = optionalTeacher.get();

            // Cập nhật các trường thông tin cơ bản
            existingTeacher.setPhoneNumber(updatedTeacherData.getPhoneNumber());
            existingTeacher.setName(updatedTeacherData.getName());
            existingTeacher.setEmail(updatedTeacherData.getEmail());
            existingTeacher.setMaGv(updatedTeacherData.getMaGv());
            existingTeacher.setCccd(updatedTeacherData.getCccd());
            existingTeacher.setDateOfBirth(updatedTeacherData.getDateOfBirth());
            existingTeacher.setAddress(updatedTeacherData.getAddress());
            existingTeacher.setGender(updatedTeacherData.getGender());

            // --- Sửa đổi phần xử lý Department ---
            if (departmentName != null && !departmentName.trim().isEmpty()) {
                // Sử dụng .orElseThrow() để trích xuất Department từ Optional
                // hoặc ném lỗi nếu không tìm thấy
                Department department = departmentRepo.findByName(departmentName)
                        .orElseThrow(() -> new RuntimeException("Department with name '" + departmentName + "' not found."));
                existingTeacher.setDepartment(department);
            } else {
                // Nếu departmentName rỗng hoặc null, đặt Department của giáo viên về null
                existingTeacher.setDepartment(null);
            }
            // ------------------------------------

            return teacherRepo.save(existingTeacher);
        } else {
            throw new RuntimeException("Không tìm thấy giáo viên với ID: " + id);
        }
    }

    public void deleteTeacher(int id) {
        Optional<Teacher> teacherOptional = teacherRepo.findById(id);
        if (teacherOptional.isPresent()) {
            teacherRepo.deleteById(id);
        }
    }

    public Set<Clazz> getClazzesByMaGvAndSemesterName(String maGv, String semesterName) {
        Optional<Teacher> teacher = teacherRepo.findByMaGv(maGv);
        return teacher.map(value -> value.getClazzes().stream()
                .filter(clazz -> clazz.getSemester().getName().equals(semesterName))
                .collect(Collectors.toSet())).orElse(null);
    }

    public int getNumberOfClassesByTeacher(int teacherId) {
        Optional<Teacher> optionalTeacher = teacherRepo.findById(teacherId);
        if (optionalTeacher.isPresent()) {
            return teacherRepo.countclazzesByTeacher(teacherId);
        } else {
            throw new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId);
        }
    }

    public Set<Clazz> getClazzesByMaGv(String maGv) {
        Optional<Teacher> optionalTeacher = teacherRepo.findByMaGv(maGv);
        if (optionalTeacher.isPresent()) {
            return teacherRepo.getClazzes(maGv);
        } else {
            throw new RuntimeException("Không tìm thấy giáo viên với ma: " + maGv);
        }
    }

    public Set<String> getModulesByMaGvAndSemester(String maGv, String semesterName) {
        Set<Clazz> clazzes = getClazzesByMaGvAndSemesterName(maGv, semesterName);
        if (clazzes == null) {
            return Set.of();
        }
        return clazzes.stream()
                .map(Clazz::getCourse) // Lấy Course từ Clazz
                .map(Course::getMaHocPhan) // Lấy MaHocPhan từ Course
                .collect(Collectors.toSet());
    }

    public Optional<Teacher> getTeacherByUsername(String username) {
        return teacherRepo.findByUser_Username(username);
    }

    public void processTeacherExcel(MultipartFile file) throws IOException {
        teacherImport.importTeachersFromExcel(file);
        teacherRepo.findAll();
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