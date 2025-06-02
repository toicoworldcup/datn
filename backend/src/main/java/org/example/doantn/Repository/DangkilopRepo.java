package org.example.doantn.Repository;

import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Dangkilop;
import org.example.doantn.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DangkilopRepo extends JpaRepository<Dangkilop, Integer> {
    boolean existsByStudentAndClazz(Student student, Clazz clazz);
    @Query("SELECT d FROM Dangkilop d WHERE d.student.mssv = :mssv AND d.semester.name = :semesterName")
    List<Dangkilop> findByStudent_MssvAndSemesterName(@Param("mssv") String mssv, @Param("semesterName") String semesterName);
    @Query("SELECT d FROM Dangkilop d WHERE d.clazz.maLop = :maLop AND d.semester.name = :semesterName And d.student.mssv = :mssv")
    Optional<Dangkilop> findByClazzMaLopAndSemesterNameAndMssv(String maLop, String semesterName,String mssv);
    List<Dangkilop> findByClazz(Clazz clazz); // Thêm dòng này



}
