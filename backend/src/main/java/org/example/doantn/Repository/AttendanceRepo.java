package org.example.doantn.Repository;

import org.example.doantn.Entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, Integer> {
    @Query("SELECT a FROM Attendance a WHERE a.clazz.id = :clazzId AND a.attendanceDate = :date")
    List<Attendance> findByClazzIdAndDate(@Param("clazzId") Integer clazzId, @Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.clazz.maLop = :maLop AND a.student.mssv = :mssv AND a.semester.name = :hocki")
    List<Attendance> findByClazz_MaLopAndStudent_MssvAndSemeter_Name(@Param("maLop") String maLop,
                                                                     @Param("mssv") String mssv,
                                                                     @Param("hocki") String hocki);

}
