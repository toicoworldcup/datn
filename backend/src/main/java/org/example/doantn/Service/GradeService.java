package org.example.doantn.Service;

import org.example.doantn.Entity.Grade;
import org.example.doantn.Entity.Semester;
import org.example.doantn.Entity.Teacher;
import org.example.doantn.Repository.GradeRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Repository.TeacherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService {
    @Autowired
    private GradeRepo gradeRepo;
    @Autowired
    private SemesterRepo semesterRepo;
    public List<Grade> getAllGradeRepos() {
        return gradeRepo.findAll();
    }
    public Grade addGrade(Grade grade) {
        Semester semester = semesterRepo.findByName(grade.getSemester().getName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kỳ học với tên: "));
        if (!semester.isOpen()) {
            throw new IllegalArgumentException("Lỗi: học kì chưa mở");
        }
        grade.setSemester(semester);
        return gradeRepo.save(grade);
    }
    public Grade updateGrade(int id, Grade updatedGrade) {
        Optional<Grade> optionalGrade = gradeRepo.findById(id);
        if (optionalGrade.isPresent()) {
            Grade existingGrade = optionalGrade.get();
            existingGrade.setDiemCk(updatedGrade.getDiemCk());
            existingGrade.setDiemGk(updatedGrade.getDiemGk());

            return gradeRepo.save(existingGrade);
        } else {
            throw new RuntimeException("Không tìm thấy học viên với ID: " + id);
        }
    }

    public void deleteGrade(int id) {
        Optional<Grade> gradeOptional = gradeRepo.findById(id);
        if(gradeOptional.isPresent()) {
            gradeRepo.deleteById(id);
        }
    }
    public Optional<Grade> getGradeByClazzIdAndStudentId(Integer clazzId, Integer studentId) {
        return gradeRepo.findByClazzIdAndStudentId(clazzId, studentId);
    }

    public List<Grade> getGradeByMssvAndSemester(String mssv, String semester) {
        return gradeRepo.findByMssvAndSemester(mssv, semester);
    }



}
