package org.example.doantn.Repository;

import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClazzRepo extends JpaRepository<Clazz, Integer> {
    List<Clazz> findBySemester(Semester semester);

    Optional<Clazz> findByMaLopAndSemester_Name(String maLop, String semesterName);

    Optional<Clazz> findByMaLop(String maLop);

    boolean existsByCourse_MaHocPhanAndSemester_NameStartingWith(String maHocPhan, String hocKi);

    @Query("SELECT t.maGv, COUNT(c) FROM Clazz c JOIN c.teachers t GROUP BY t.maGv")
    List<Object[]> countClazzesByTeacher();

    @Query("SELECT c FROM Clazz c " +
            "JOIN c.semester s " +
            "JOIN c.course co " +
            "JOIN co.ctdts ct " +
            "JOIN ct.batch b " +
            "WHERE s.name = :hocKi AND ct.maCt = :ctdtCode AND b.name = :khoa")
    List<Clazz> findClazzesByCtdtCodeAndKhoaAndHocKi(
            @Param("ctdtCode") String ctdtCode,
            @Param("khoa") String khoa,
            @Param("hocKi") String hocKi
    );
}