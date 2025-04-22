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
import java.util.Optional;

public interface ScheduleRepo extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByDayOfWeek(String dayOfWeek);
    List<Schedule> findByClazz_MaLop(String maLop);
    boolean existsByRoomAndTimeSlotAndDayOfWeekAndSemester(Room room, TimeSlot timeslot, String dayOfWeek, Semester semester);
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.semester.id = :semesterId")
    int countSchedulesBySemesterId(@Param("semesterId") Integer semesterId);
    @Modifying
    @Transactional
    @Query("DELETE FROM Schedule s WHERE s.semester.id IN (SELECT sem.id FROM Semester sem WHERE sem.name = :semesterName)")
    void deleteBySemesterName(@Param("semesterName") String semesterName);



}
