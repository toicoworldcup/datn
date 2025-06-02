package org.example.doantn.Service;

import org.example.doantn.Entity.*;
import org.example.doantn.Repository.ClazzRepo;
import org.example.doantn.Repository.GradeRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Repository.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        if (score >= 9.0) return "A+";
        if (score >= 8.5) return "A";
        if (score >= 8.0) return "B+";
        if (score >= 7.0) return "B";
        if (score >= 6.5) return "C+";
        if (score >= 5.5) return "C";
        if (score >= 5.0) return "D+";
        if (score >= 4.0) return "D";
        return "F";
    }
}
