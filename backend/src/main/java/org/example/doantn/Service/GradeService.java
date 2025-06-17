package org.example.doantn.Service;

import org.example.doantn.Entity.*;
import org.example.doantn.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GradeService {
    @Autowired
    private GradeRepo gradeRepo;
    @Autowired
    private SemesterRepo semesterRepo;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private ClazzRepo clazzRepo;
    @Autowired
    private DangkilopRepo dangkilopRepo;
    @Autowired
    private DangkihocphanService dangkihocphanService;

    public List<Grade> getAllGradeRepos() {
        return gradeRepo.findAll();
    }

    public Grade addGrade(Grade grade) {
        Semester semester = semesterRepo.findByName(grade.getSemester().getName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kỳ học với tên: " + grade.getSemester().getName()));
        if (!semester.isOpen()) {
            throw new IllegalArgumentException("Lỗi: học kì chưa mở");
        }
        grade.setSemester(semester);
        return gradeRepo.save(grade);
    }



    @Transactional
    public Grade updateGrade(String mssv, String maLop, String semesterName, Grade updatedGrade) {
        Student student = studentRepo.findByMssv(mssv)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với mssv: " + mssv));
        Clazz clazz = clazzRepo.findByMaLopAndSemester_Name(maLop, semesterName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp " + maLop + " trong học kỳ " + semesterName));
        Semester semester = semesterRepo.findByName(semesterName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với tên: " + semesterName));

        Optional<Grade> optionalGrade = gradeRepo.findByStudentAndClazzAndSemester(student, clazz, semester);
        if (optionalGrade.isPresent()) {
            Grade existingGrade = optionalGrade.get();
            Double oldGk = existingGrade.getDiemGk();
            Double oldCk = existingGrade.getDiemCk();

            existingGrade.setDiemGk(updatedGrade.getDiemGk());
            existingGrade.setDiemCk(updatedGrade.getDiemCk());

            existingGrade.appendHistory(oldGk, updatedGrade.getDiemGk(), oldCk, updatedGrade.getDiemCk());

            return gradeRepo.save(existingGrade);
        } else {
            Grade newGrade = new Grade();
            newGrade.setStudent(student);
            newGrade.setClazz(clazz);
            newGrade.setSemester(semester);
            newGrade.setDiemGk(updatedGrade.getDiemGk());
            newGrade.setDiemCk(updatedGrade.getDiemCk());
            return gradeRepo.save(newGrade);
        }
    }


    public void deleteGrade(int id) {
        Optional<Grade> gradeOptional = gradeRepo.findById(id);
        if (gradeOptional.isPresent()) {
            gradeRepo.deleteById(id);
        }
    }


    public List<Grade> getGradeByMssvAndSemester(String mssv, String semester) {
        return gradeRepo.findByMssvAndSemester(mssv, semester);
    }

    public List<Grade> getGradesByStudentAndSemester(String mssv, String semesterName) {
        return gradeRepo.findByStudent_MssvAndSemester_Name(mssv, semesterName);
    }
    // Phương thức để tính toán điểm học phần và xếp loại
    public void calculateFinalGradeAndLetter(Dangkihocphan dkhp) {
        Double diemGK = dkhp.getGki();
        Double diemCK = dkhp.getCki();
        if (dkhp.getCourse() != null) {
            String gradeRatio = dkhp.getCourse().getGradeRatio();
            if (diemGK != null && diemCK != null && gradeRatio != null) {
                String[] ratios = gradeRatio.split("-");
                if (ratios.length == 2) {
                    try {
                        double ratioGK = Double.parseDouble(ratios[0]) / 10.0;
                        double ratioCK = Double.parseDouble(ratios[1]) / 10.0;
                        double finalScore = diemGK * ratioGK + diemCK * ratioCK;
                        dkhp.setFinalGrade(finalScore);
                        dkhp.setGradeLetter(convertScoreToLetter(finalScore));
                    } catch (NumberFormatException e) {
                        System.err.println("Lỗi định dạng tỉ lệ điểm cho học phần " + dkhp.getCourse().getMaHocPhan());
                    }
                } else {
                    System.err.println("Sai định dạng tỉ lệ điểm cho học phần " + dkhp.getCourse().getMaHocPhan());
                }
            } else {
                dkhp.setFinalGrade(null);
                dkhp.setGradeLetter(null);
            }
        }
    }


    private String convertScoreToLetter(double score) {
        if (score >= 9.2) return "A+";
        if (score >= 8.5) return "A";
        if (score >= 7.7) return "B+";
        if (score >= 7.0) return "B";
        if (score >= 6.2) return "C+";
        if (score >= 5.5) return "C";
        if (score >= 4.7) return "D+";
        if (score >= 4.0) return "D";
        return "F";
    }

    // Phương thức mới để chuyển đổi điểm chữ sang điểm GPA dựa trên bảng quy đổi đã cung cấp
    private double convertLetterToGpaPoint(String letter) {
        switch (letter) {
            case "A+": return 4.0;
            case "A": return 3.7;
            case "B+": return 3.5;
            case "B": return 3.0;
            case "C+": return 2.5;
            case "C": return 2.0;
            case "D+": return 1.5;
            case "D": return 1.0;
            case "F": return 0.0;
            default: return 0.0; // Trường hợp không xác định, mặc định là 0
        }
    }
    public double calculateSemesterGPA(String mssv, String semesterName) {
        // Bước 1: Tìm sinh viên và học kỳ
        Student student = studentRepo.findByMssv(mssv)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với mssv: " + mssv));
        Semester semester = semesterRepo.findByName(semesterName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với tên: " + semesterName));

        // Bước 2: Lấy tất cả các đăng ký lớp của sinh viên trong học kỳ này
        List<Dangkilop> dangkilops = dangkilopRepo.findByStudentAndSemester(student, semester);

        if (dangkilops.isEmpty()) {
            return 0.0; // Không có lớp nào được đăng ký cho sinh viên này trong học kỳ này
        }

        // Thay đổi từ double sang BigDecimal để tính toán chính xác
        BigDecimal totalWeightedGradePoints = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        // Bước 3: Duyệt qua từng đăng ký lớp
        for (Dangkilop dkLop : dangkilops) {
            Clazz clazz = dkLop.getClazz();
            if (clazz == null || clazz.getCourse() == null) {
                System.err.println("Cảnh báo: Thông tin lớp hoặc học phần bị thiếu cho đăng ký lớp ID: " + dkLop.getId());
                continue; // Bỏ qua nếu không có thông tin cần thiết
            }

            Course course = clazz.getCourse();
            // Lấy số tín chỉ, chuyển sang BigDecimal
            BigDecimal credits = new BigDecimal(course.getTinChi());

            // Bước 4: Tìm điểm tương ứng (Grade) cho đăng ký lớp này
            Optional<Grade> optionalGrade = gradeRepo.findByStudentAndClazzAndSemester(student, clazz, semester);

            if (optionalGrade.isPresent()) {
                Grade grade = optionalGrade.get();

                // Bước 5: Tính điểm cuối cùng và điểm chữ cho điểm này
                // Tạo một đối tượng tạm thời mô phỏng Dangkihocphan để sử dụng calculateFinalGradeAndLetter
                Dangkihocphan tempDkhp = new Dangkihocphan();
                tempDkhp.setGki(grade.getDiemGk());
                tempDkhp.setCki(grade.getDiemCk());
                tempDkhp.setCourse(course); // Thiết lập khóa học để có tỷ lệ điểm

                // Gọi lại phương thức calculateFinalGradeAndLetter để tính toán
                calculateFinalGradeAndLetter(tempDkhp);

                if (tempDkhp.getGradeLetter() != null) {
                    // Chuyển đổi gradePoint từ double sang BigDecimal
                    BigDecimal gradePoint = new BigDecimal(convertLetterToGpaPoint(tempDkhp.getGradeLetter()));

                    totalWeightedGradePoints = totalWeightedGradePoints.add(gradePoint.multiply(credits));
                    totalCredits = totalCredits.add(credits);
                } else {
                    System.err.println("Cảnh báo: Không thể tính điểm chữ cho điểm của sinh viên " + mssv + ", lớp " + clazz.getMaLop());
                }
            } else {
                System.out.println("Thông báo: Không tìm thấy điểm cho lớp " + clazz.getMaLop() + " của sinh viên " + mssv + " trong học kỳ " + semesterName);
            }
        }

        if (totalCredits.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0; // Tránh chia cho số 0 nếu không có tín chỉ nào hợp lệ
        }

        // Bước 6: Tính toán và làm tròn GPA
        // Chia với 3 chữ số thập phân, làm tròn HALF_UP (làm tròn lên nếu số sau là 5 trở lên)
        BigDecimal gpa = totalWeightedGradePoints.divide(totalCredits, 3, RoundingMode.HALF_UP);

        return gpa.doubleValue();}

    public double calculateStudentCPA(String mssv) {
        // Bước 1: Lấy danh sách các môn học đã hoàn thành với điểm số và tín chỉ.
        // DangkihocphanService đã xử lý việc chọn bản ghi mới nhất/có điểm cuối cùng.
        List<Map<String, Object>> accumulatedCourseResults = dangkihocphanService.getAccumulatedGrades(mssv);

        if (accumulatedCourseResults.isEmpty()) {
            return 0.0; // Chưa có học phần nào có điểm để tính CPA
        }

        BigDecimal totalWeightedGradePoints = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        // Bước 2: Duyệt qua từng kết quả học phần và tính toán
        for (Map<String, Object> result : accumulatedCourseResults) {
            Double finalGrade = (Double) result.get("finalGrade");
            Integer credits = (Integer) result.get("soTinChi");

            // Chỉ tính những môn có điểm cuối cùng và số tín chỉ hợp lệ
            if (finalGrade != null && credits != null && credits > 0) {
                // Sử dụng hàm convertScoreToLetter rồi mới convertLetterToGpaPoint
                // (vì getAccumulatedGrades trả về finalGrade, không phải gradeLetter)
                String gradeLetter = convertScoreToLetter(finalGrade);
                BigDecimal gradePoint = new BigDecimal(convertLetterToGpaPoint(gradeLetter));
                BigDecimal courseCredits = new BigDecimal(credits);

                totalWeightedGradePoints = totalWeightedGradePoints.add(gradePoint.multiply(courseCredits));
                totalCredits = totalCredits.add(courseCredits);
            }
        }

        if (totalCredits.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0; // Tránh chia cho 0 nếu không có tín chỉ nào được tính
        }

        // Bước 3: Tính toán và làm tròn CPA
        // Chia với 3 chữ số thập phân, làm tròn HALF_UP
        BigDecimal cpa = totalWeightedGradePoints.divide(totalCredits, 3, RoundingMode.HALF_UP);

        return cpa.doubleValue();
    }


}
