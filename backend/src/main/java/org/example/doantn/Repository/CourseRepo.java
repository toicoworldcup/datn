package org.example.doantn.Repository;

import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface CourseRepo extends JpaRepository<Course, Integer> {
    Optional<Course> findByMaHocPhan(String maHocPhan);
    @Query("SELECT c FROM Clazz c WHERE c.course.maHocPhan = :maHocPhan AND c.semester.name = :semesterName")
    Set<Clazz> getClazzesByMaHocPhanAndSemesterName(@Param("maHocPhan") String maHocPhan, @Param("semesterName") String semesterName);

    List<Course> findByCtdts_MaCt(String maCt);

    // Tìm kiếm học phần theo mã CTĐT và khóa
    @Query("SELECT c FROM Course c JOIN c.ctdts ctdt JOIN ctdt.batch b WHERE ctdt.maCt = :maCt AND b.name = :khoa")
    List<Course> findByCtdt_maCtAndKhoa(@Param("maCt") String maCt, @Param("khoa") String khoa);

    // Tìm kiếm học phần theo mã CTĐT
    @Query("SELECT c FROM Course c JOIN c.ctdts ctdt WHERE ctdt.maCt = :maCt")
    List<Course> findByCtdt_maCt(@Param("maCt") String maCt);

    // Tìm kiếm học phần theo khóa
    @Query("SELECT c FROM Course c JOIN c.ctdts ctdt JOIN ctdt.batch b WHERE b.name = :khoa")
    List<Course> findByKhoa(@Param("khoa") String khoa);


}
