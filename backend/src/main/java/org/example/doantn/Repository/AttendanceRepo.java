package org.example.doantn.Repository;

import org.example.doantn.Entity.AStatus;
import org.example.doantn.Entity.Attendance;
import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Semester;
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

    List<Attendance> findByClazzAndSemesterAndAttendanceDate(Clazz clazz, Semester semester, LocalDate attendanceDate);

    int countByClazz_MaLopAndStudent_MssvAndStatusAndAttendanceDateLessThanEqual(
            String maLop, String mssv, AStatus status, LocalDate toDate);

    List<Attendance> findByClazzAndSemester(Clazz clazz, Semester semester);

    List<Attendance> findByClazz_MaLopAndStudent_MssvAndSemester_NameAndAttendanceDate(
            @Param("maLop") String maLop,
            @Param("mssv") String mssv,
            @Param("hocki") String hocki,
            @Param("attendanceDate") LocalDate attendanceDate
    );

    boolean existsByClazzAndSemesterAndAttendanceDate(Clazz clazz, Semester semester, LocalDate attendanceDate);
}