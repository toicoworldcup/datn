package org.example.doantn.Repository;

import org.example.doantn.Entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemesterRepo extends JpaRepository<Semester, Integer> {
    Optional<Semester> findByName(String semesterName);
    Optional<Semester> findByIsOpen(boolean isOpen);
    @Query("SELECT s FROM Semester s WHERE s.name = :name AND s.isOpen = :isOpen")
    Optional<Semester> findByNameAndIsOpen(@Param("name") String name, @Param("isOpen") boolean isOpen);


}
