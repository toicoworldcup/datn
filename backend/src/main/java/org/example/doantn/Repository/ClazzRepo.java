package org.example.doantn.Repository;

import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Semester;
import org.example.doantn.Entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClazzRepo extends JpaRepository<Clazz, Integer> {
    List<Clazz> findBySemester(Semester semester);

    Optional<Clazz> findByMaLopAndSemester_Name(String maLop, String semesterName);

    @Query("SELECT t.maGv, COUNT(c) FROM Clazz c JOIN c.teachers t GROUP BY t.maGv")
    List<Object[]> countClazzesByTeacher();

}
