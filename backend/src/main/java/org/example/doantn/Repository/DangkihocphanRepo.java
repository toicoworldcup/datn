package org.example.doantn.Repository;

import org.example.doantn.Entity.Course;
import org.example.doantn.Entity.Dangkihocphan;
import org.example.doantn.Entity.Semester;
import org.example.doantn.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface DangkihocphanRepo extends JpaRepository<Dangkihocphan, Integer> {
    List<Dangkihocphan> findByStudent(Student student);
    @Query("SELECT d FROM Dangkihocphan d WHERE d.student.mssv = :mssv AND d.semester.name = :semesterName")
    List<Dangkihocphan> findByStudent_MssvAndSemesterName(@Param("mssv") String mssv, @Param("semesterName") String semesterName);


    @Query("SELECT d FROM Dangkihocphan d WHERE d.student.mssv = :mssv AND d.course.maHocPhan = :mahp")
    List<Dangkihocphan> findByStudent_MssvAndMaHocPhan(@Param("mssv") String mssv, @Param("mahp") String mahp);

    List<Dangkihocphan> findByStudent_Mssv(String mssv);

    @Query("SELECT COUNT(d) FROM Dangkihocphan d WHERE d.course.maHocPhan = :maHocPhan")
    long countByCourseMaHocPhan(@Param("maHocPhan") String maHocPhan);
    int countByCourse_MaHocPhanAndSemester_Name(String maHocPhan, String name);


}
