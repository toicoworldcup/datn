package org.example.doantn.Repository;

import org.example.doantn.Entity.Grade;
import org.example.doantn.Entity.Student;
import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepo extends JpaRepository<Grade, Integer> {
    @Query("SELECT g FROM Grade g " +
            "JOIN g.clazz c " +
            "JOIN c.semester s " +
            "JOIN g.student st " +
            "WHERE st.mssv = :mssv AND s.name = :semesterName")
    List<Grade> findByMssvAndSemester(@Param("mssv") String mssv, @Param("semesterName") String semesterName);

    // Phương thức mới để tìm Grade theo Student, Clazz, Semester
    Optional<Grade> findByStudentAndClazzAndSemester(Student student, Clazz clazz, Semester semester);

    List<Grade> findByStudent_MssvAndSemester_Name(String mssv, String semesterName);

}
