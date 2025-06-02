package org.example.doantn.Repository;

import jakarta.transaction.Transactional;
import org.example.doantn.Entity.Room;
import org.example.doantn.Entity.Schedule;
import org.example.doantn.Entity.Semester;
import org.example.doantn.Entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepo extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByClazz_MaLop(String maLop);
    List<Schedule> findBySemester(Semester semester); // Thêm phương thức này
    boolean existsByRoomAndTimeSlotAndDayOfWeekAndSemester(Room room, TimeSlot timeslot, String dayOfWeek, Semester semester);
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.semester.id = :semesterId")
    int countSchedulesBySemesterId(@Param("semesterId") Integer semesterId);
    @Modifying
    @Transactional
    @Query("DELETE FROM Schedule s WHERE s.semester.id IN (SELECT sem.id FROM Semester sem WHERE sem.name = :semesterName)")
    void deleteBySemesterName(@Param("semesterName") String semesterName);
    List<Schedule> findByClazz_MaLopAndSemester_Name(String maLop, String semesterName);
    @Query("SELECT DISTINCT s FROM Schedule s JOIN s.clazz c JOIN c.teachers t WHERE t.maGv = :teacherId AND s.semester.name = :semesterName")    List<Schedule> findSchedulesByTeacherMaGvAndSemesterName(@Param("teacherId") String teacherId, @Param("semesterName") String semesterName);

    @Query("SELECT s FROM Schedule s " +
            "WHERE s.clazz.maLop IN (SELECT dkl.clazz.maLop FROM Dangkilop dkl WHERE dkl.student.mssv = :studentMssv) " +
            "AND s.semester.name = :semesterName")
    List<Schedule> findSchedulesByStudentMssvAndSemesterName(
            @Param("studentMssv") String studentMssv,
            @Param("semesterName") String semesterName
    );
}