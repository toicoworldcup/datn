package org.example.doantn.Service;

import org.example.doantn.Dto.response.ClassTeacherDTO;
import org.example.doantn.Dto.response.ClazzDTO;
import org.example.doantn.Dto.response.TeacherDTO;
import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Course;
import org.example.doantn.Entity.Semester;
import org.example.doantn.Entity.Teacher;
import org.example.doantn.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClazzService {
    @Autowired
    private ClazzRepo clazzRepo;
    @Autowired
    private TeacherRepo teacherRepo;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private SemesterRepo semesterRepo;
    @Autowired
    private DangkihocphanRepo dangkihocphanRepo;

    // Lấy danh sách tất cả các lớp học
    public List<Clazz> getAllClazzes() {
        return clazzRepo.findAll();
    }

    // Lấy thông tin lớp học theo ma
    public Clazz getClazzByMaLopAndSemester(String maLop,String semesterName) {
        return clazzRepo.findByMaLopAndSemester_Name(maLop,semesterName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học với ma: " + maLop));
    }

    public Map<String, Integer> countClazzesPerTeacher() {
        List<Object[]> results = clazzRepo.countClazzesByTeacher();
        Map<String, Integer> teacherClazzCount = new HashMap<>();

        for (Object[] result : results) {
            String teacherId = (String) result[0];  // Lấy mã giáo viên
            Integer clazzCount = ((Number) result[1]).intValue();  // Lấy số lớp
            teacherClazzCount.put(teacherId, clazzCount);
        }

        return teacherClazzCount;
    }

    // Thêm mới lớp học
    public Clazz addClazz(Clazz clazz) {
        if (clazz.getCourse() != null && clazz.getCourse().getId() != 0) {
            clazz.setCourse(courseRepo.findById(clazz.getCourse().getId())
                    .orElseThrow(() -> new RuntimeException("course not found")));
        } else {
            clazz.setCourse(null);
        }
        if (clazz.getSemester() != null && clazz.getSemester().getId() != 0) {
            clazz.setSemester(semesterRepo.findById(clazz.getSemester().getId())
                    .orElseThrow(() -> new RuntimeException("semester not found")));
        } else {
            clazz.setSemester(null);
        }
        return clazzRepo.save(clazz);
    }

    // Cập nhật thông tin lớp học
    public Clazz updateClazz(Integer id, String maLop, LocalDate lichThi) {
        Clazz existingClazz = clazzRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học với ID: " + id));

        if (maLop != null) {
            existingClazz.setMaLop(maLop);
        }
        if (lichThi != null) {
            existingClazz.setLichThi(lichThi);
        }

        return clazzRepo.save(existingClazz);
    }

    // Xóa lớp học theo ID
    public void deleteClazz(Integer id) {
        if (!clazzRepo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy lớp học với ID: " + id);
        }
        clazzRepo.deleteById(id);
    }
    //phan cong giao vien cho lop hoc
    public Clazz assignTeacherToClazz(String maLop, String maGv,String semesterName) {
        Clazz clazz = clazzRepo.findByMaLopAndSemester_Name(maLop,semesterName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + maLop));

        Teacher teacher = teacherRepo.findByMaGv(maGv)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + maGv));

        Semester semester = semesterRepo.findByName(semesterName)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kỳ học với tên: "));
        if (!semester.isOpen()) {
            throw new IllegalArgumentException("Lỗi: học kì chưa mở");
        }

        clazz.getTeachers().add(teacher);
        teacher.getClazzes().add(clazz);

        return clazzRepo.save(clazz);
    }

    public List<TeacherDTO> getTeachersByClazz(String maLop, String hocKi) {
        Clazz clazz = clazzRepo.findByMaLopAndSemester_Name(maLop, hocKi).orElse(null);
        if (clazz != null) {
            return clazz.getTeachers().stream()
                    .map(TeacherDTO::new)
                    .collect(Collectors.toList());
        }
        return null; // Hoặc throw một exception nếu bạn muốn xử lý trường hợp không tìm thấy lớp
    }

    public List<ClassTeacherDTO> getTeacherCodesByClassDTO() {
        List<Clazz> allClazzes = clazzRepo.findAll();
        List<ClassTeacherDTO> result = new ArrayList<>();

        for (Clazz clazz : allClazzes) {
            if (clazz.getTeachers() != null) {
                List<String> teacherCodes = clazz.getTeachers().stream()
                        .map(teacher -> teacher.getMaGv())
                        .collect(Collectors.toList());
                String classIdentifier = clazz.getMaLop() + " - " + (clazz.getSemester() != null ? clazz.getSemester().getName() : "N/A");
                result.add(new ClassTeacherDTO(classIdentifier, teacherCodes));
            } else {
                String classIdentifier = clazz.getMaLop() + " - " + (clazz.getSemester() != null ? clazz.getSemester().getName() : "N/A");
                result.add(new ClassTeacherDTO(classIdentifier, new ArrayList<>()));
            }
        }

        return result;
    }


    public List<ClazzDTO> getUnassignedClazzes() {
        List<Clazz> unassignedClazzes = clazzRepo.findByTeachersIsEmpty();
        return unassignedClazzes.stream()
                .map(ClazzDTO::new)
                .collect(Collectors.toList());
    }

    public void generateClazzForCourse(String maHocPhan, String hocki) {
        Optional<Course> courseOpt = courseRepo.findByMaHocPhan(maHocPhan);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy học phần có mã: " + maHocPhan);
        }

        Course course = courseOpt.get();

        Optional<Semester> semesterOpt = semesterRepo.findByName(hocki);
        if (semesterOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy học kỳ: " + hocki);
        }

        Semester semester = semesterOpt.get();

        int maxStudentsPerClazz = 50;

        int totalStudents = dangkihocphanRepo.countByCourse_MaHocPhanAndSemester_Name(maHocPhan, hocki);
        if (totalStudents == 0) {
            throw new RuntimeException("Chưa có sinh viên nào đăng ký học phần này trong học kỳ đã chọn.");
        }

        int numberOfClazzes = (int) Math.ceil((double) totalStudents / maxStudentsPerClazz);

        for (int i = 1; i <= numberOfClazzes; i++) {
            Clazz clazz = new Clazz();
            clazz.setMaLop(maHocPhan + "-" + hocki.replaceAll("\\s+", "") + "-" + i);  // Mã lớp ví dụ: CT001-HK1-1
            clazz.setCourse(course);
            clazz.setSemester(semester);
            clazzRepo.save(clazz);
        }
    }



}
