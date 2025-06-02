package org.example.doantn.Service;

import jakarta.transaction.Transactional;
import org.example.doantn.Dto.request.AssignmentRequest;
import org.example.doantn.Dto.response.*;
import org.example.doantn.Entity.*;
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
    @Autowired
    private DangkilopRepo dangkilopRepo;
    @Autowired
    private CtdtRepo ctdtRepo;
    @Autowired
    private BatchRepo batchRepo;
    @Autowired
    private GradeRepo gradeRepo;
    @Autowired
    private TeacherService teacherService; // Inject TeacherService

    // Lấy danh sách tất cả các lớp học
    public List<Clazz> getAllClazzes() {
        return clazzRepo.findAll();
    }

    // Lấy thông tin lớp học theo ma
    public Clazz getClazzByMaLopAndSemester(String maLop, String semesterName) {
        return clazzRepo.findByMaLopAndSemester_Name(maLop, semesterName)
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


    public List<Clazz> findClazzesByCriteria(String ctdtCode, String khoa, String hocKi) {
        // Log trước khi gọi repository
        System.out.println("Tìm kiếm lớp học với ctdtCode: " + ctdtCode + ", khoa: " + khoa + ", hocKi: " + hocKi);

        List<Clazz> clazzes = clazzRepo.findClazzesByCtdtCodeAndKhoaAndHocKi(ctdtCode, khoa, hocKi);

        // Log sau khi gọi repository
        System.out.println("Kết quả từ repository: " + clazzes);

        return clazzes;
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

    @Transactional
    public void assignTeachersToClazzes(List<AssignmentRequest> assignmentRequests) {
        for (AssignmentRequest request : assignmentRequests) {
            String maLop = request.getMaLop();
            String maGv = request.getMaGv();
            String hocKi = request.getHocKi();

            Clazz clazz = clazzRepo.findByMaLopAndSemester_Name(maLop, hocKi)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp với mã lớp: " + maLop + " trong học kỳ: " + hocKi));

            Teacher giangVien = teacherRepo.findByMaGv(maGv)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giảng viên với mã GV: " + maGv));

            // Kiểm tra xung đột thời khóa biểu
            Set<Clazz> teacherSchedule = teacherService.getClazzesByMaGvAndSemesterName(maGv, hocKi);
            if (teacherSchedule != null && teacherSchedule.stream().anyMatch(c -> c.getMaLop().equals(clazz.getMaLop()))) {
                throw new IllegalArgumentException("Giáo viên " + maGv + " đã được phân công cho lớp " + maLop + " trong học kỳ " + hocKi + ".");
            }

            // Thêm giảng viên vào tập hợp (nếu quan hệ nhiều-nhiều)
            if (clazz.getTeachers() == null) {
                clazz.setTeachers(new HashSet<>());
            }
            clazz.getTeachers().add(giangVien);
            clazzRepo.save(clazz);
        }
    }

    @Transactional
    //phan cong giao vien cho lop hoc
    public void assignTeacherToClazz(String maLop, String maGv, String semesterName) {
        Clazz clazzToAssign = clazzRepo.findByMaLopAndSemester_Name(maLop, semesterName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học với mã lớp: " + maLop + " trong học kỳ: " + semesterName));

        Teacher teacher = teacherRepo.findByMaGv(maGv)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với mã GV: " + maGv));

        Semester semester = semesterRepo.findByName(semesterName)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kỳ học với tên: " + semesterName));
        if (!semester.isOpen()) {
            throw new IllegalArgumentException("Lỗi: học kì " + semesterName + " chưa mở");
        }

        // Kiểm tra xung đột thời khóa biểu
        Set<Clazz> teacherSchedule = teacherService.getClazzesByMaGvAndSemesterName(maGv, semesterName);
        if (teacherSchedule != null && teacherSchedule.stream().anyMatch(clazz -> clazz.getMaLop().equals(clazzToAssign.getMaLop()))) {
            throw new IllegalArgumentException("Giáo viên " + maGv + " đã được phân công cho lớp " + maLop + " trong học kỳ " + semesterName + ".");
        }

        clazzToAssign.getTeachers().add(teacher);
        teacher.getClazzes().add(clazzToAssign);

        clazzRepo.save(clazzToAssign);
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
                        .map(Teacher::getMaGv)
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


    public List<StudentGradeDTO> getStudentsByClazzAndSemester(String maLop, String hocki) {
        Clazz clazz = clazzRepo.findByMaLopAndSemester_Name(maLop, hocki).orElse(null);
        if (clazz != null) {
            List<Dangkilop> dangkilops = dangkilopRepo.findByClazz(clazz);
            Semester semester = semesterRepo.findByName(hocki).orElse(null);
            return dangkilops.stream()
                    .map(Dangkilop::getStudent)
                    .map(student -> {
                        StudentGradeDTO studentGradeDTO = new StudentGradeDTO();
                        studentGradeDTO.setMaLop(maLop);
                        studentGradeDTO.setSemesterName(hocki);
                        studentGradeDTO.setName(student.getName());
                        studentGradeDTO.setMssv(student.getMssv());

                        Grade grade = gradeRepo.findByStudentAndClazzAndSemester(student, clazz, semester).orElse(null);
                        if (grade != null) {
                            studentGradeDTO.setDiemGk(grade.getDiemGk());
                            studentGradeDTO.setDiemCk(grade.getDiemCk());
                            studentGradeDTO.setHistory(grade.getHistory()); // Thêm dòng này
                        }
                        return studentGradeDTO;
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }


    public List<ClazzDTO> getUnassignedClazzes(String ctdtCode, String khoa, String hocKi) {
        // 1. Lấy các lớp học dựa trên tiêu chí tìm kiếm
        List<Clazz> filteredClazzes = clazzRepo.findClazzesByCtdtCodeAndKhoaAndHocKi(ctdtCode, khoa, hocKi);

        // 2. Lọc ra các lớp trong kết quả tìm kiếm mà không có giáo viên
        return filteredClazzes.stream()
                .filter(clazz -> clazz.getTeachers().isEmpty())
                .map(ClazzDTO::new) // Giả sử ClazzDTO có constructor nhận Clazz
                .collect(Collectors.toList());
    }

    public int generateClazzesForCTDTCodeAndKhoa(String ctdtCode, String khoa, String hocKi) {
        Optional<Ctdt> ctdtOptional = ctdtRepo.findByMaCt(ctdtCode);
        if (ctdtOptional.isEmpty()) {
            throw new RuntimeException("Không tìm thấy CTĐT với mã: " + ctdtCode);
        }
        Ctdt ctdt = ctdtOptional.get();

        Optional<Batch> batchOptional = batchRepo.findByName(khoa);
        if (batchOptional.isEmpty()) {
            throw new RuntimeException("Không tìm thấy khóa học: " + khoa);
        }
        Optional<Semester> semesterOptional = semesterRepo.findByName(hocKi);
        if (semesterOptional.isEmpty()) {
            throw new RuntimeException("Không tìm thấy học kỳ: " + hocKi);
        }
        Semester semester = semesterOptional.get();

        Set<Course> coursesInCTDT = ctdt.getCourses();
        if (coursesInCTDT.isEmpty()) {
            throw new RuntimeException("Không có học phần nào thuộc CTĐT có mã: " + ctdtCode);
        }

        int maxStudentsPerClazz = 50;
        int classesCreatedCount = 0;

        for (Course course : coursesInCTDT) {
            String maHocPhan = course.getMaHocPhan();

            // Kiểm tra xem lớp học cho học phần, khóa, học kỳ đã tồn tại chưa
            if (clazzRepo.existsByCourse_MaHocPhanAndSemester_NameStartingWith(maHocPhan, hocKi)) {
                System.out.println("Lớp học cho học phần " + maHocPhan + ", khóa " + khoa + ", học kỳ " + hocKi + " đã tồn tại.");
                continue; // Bỏ qua nếu đã tồn tại
            }

            int totalStudents = dangkihocphanRepo.countByCourse_MaHocPhanAndSemester_Name(maHocPhan, hocKi);

            if (totalStudents > 0) {
                int numberOfClazzes = (int) Math.ceil((double) totalStudents / maxStudentsPerClazz);

                for (int i = 1; i <= numberOfClazzes; i++) {
                    Clazz clazz = new Clazz();
                    clazz.setMaLop(maHocPhan + "-" + khoa.replaceAll("\\s+", "") + "-" + hocKi.replaceAll("\\s+", "") + "-" + i);
                    clazz.setCourse(course);
                    clazz.setSemester(semester);
                    clazzRepo.save(clazz);
                    classesCreatedCount++;
                }
            }
        }
        return classesCreatedCount;
    }

    public List<TeacherDTO> getAvailableTeachersForClazz(String maLop, String hocKi) {
        Clazz clazz = clazzRepo.findByMaLopAndSemester_Name(maLop, hocKi)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với mã lớp: " + maLop + " trong học kỳ: " + hocKi));
        List<Teacher> allTeachers = teacherRepo.findAll();
        List<TeacherDTO> availableTeachers = new ArrayList<>();

        for (Teacher teacher : allTeachers) {
            Set<Clazz> teacherSchedule = teacherService.getClazzesByMaGvAndSemesterName(teacher.getMaGv(), hocKi);
            // Kiểm tra xem giáo viên có trùng lịch với lớp hiện tại không
            boolean isConflict = false;
            if (teacherSchedule != null) {
                for (Clazz scheduledClazz : teacherSchedule) {
                    if (scheduledClazz.getMaLop().equals(clazz.getMaLop())) {
                        isConflict = true;
                        break;
                    }
                }
            }
            if (!isConflict) {
                availableTeachers.add(new TeacherDTO(teacher));
            }
        }
        return availableTeachers;
    }
}