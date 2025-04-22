package org.example.doantn.Repository;

import org.example.doantn.Entity.Student;
import org.example.doantn.Entity.Teacher;
import org.example.doantn.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepo extends JpaRepository<Student, Integer> {
    @Query("SELECT s FROM Student s WHERE s.mssv LIKE %:keyword%")
    Optional<Student> findByMssv(@Param("keyword") String keyword);
    Optional<Student> findByUser(User user);

    Optional<Student> findByUser_Username(String username);


    List<Student> findByBatchName(String batchName);

    boolean existsByMssv(String mssv);
}
