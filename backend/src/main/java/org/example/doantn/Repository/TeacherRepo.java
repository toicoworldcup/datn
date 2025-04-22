package org.example.doantn.Repository;

import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Teacher;
import org.example.doantn.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TeacherRepo extends JpaRepository<Teacher, Integer> {
    @Query(value= "SELECT COUNT(clazz_id) FROM teacher_clazz  WHERE teacher_id=:teacherId", nativeQuery = true)
    int countclazzesByTeacher(@Param("teacherId") int teacherId );

    @Query("SELECT t.clazzes FROM Teacher t WHERE t.maGv = :maGv")
    Set<Clazz> getClazzes(@Param("maGv") String maGv);
    Optional<Teacher> findByMaGv(String maGv);
    Optional<Teacher> findByUser_Username(String username);


}
