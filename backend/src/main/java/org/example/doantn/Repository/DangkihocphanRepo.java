package org.example.doantn.Repository;

import org.example.doantn.Entity.Dangkihocphan;
import org.example.doantn.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DangkihocphanRepo extends JpaRepository<Dangkihocphan, Integer> {
    List<Dangkihocphan> findByStudent(Student student);
    @Query("SELECT d FROM Dangkihocphan d WHERE d.student.mssv = :mssv AND d.semester.name = :semesterName")
    List<Dangkihocphan> findByStudent_MssvAndSemesterName(@Param("mssv") String mssv, @Param("semesterName") String semesterName);

    List<Dangkihocphan> findByStudent_Mssv(String mssv);

    @Query("SELECT d FROM Dangkihocphan d WHERE d.student.mssv = :mssv AND d.course.maHocPhan = :mahp AND d.semester.name = :semesterName")
    Optional<Dangkihocphan> findByStudent_MssvAndCourse_MaHocPhanAndSemester_Name(
            @Param("mssv") String mssv,
            @Param("mahp") String mahp,
            @Param("semesterName") String semesterName
    );

    int countByCourse_MaHocPhanAndSemester_Name(String maHocPhan, String name);
    List<Dangkihocphan> findByStudent_MssvAndCourse_MaHocPhan(String mssv, String maHocPhan);

}