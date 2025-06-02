package org.example.doantn.Repository;

import org.example.doantn.Entity.Batch;
import org.example.doantn.Entity.Course;
import org.example.doantn.Entity.Ctdt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface CtdtRepo extends JpaRepository<Ctdt, Integer> {
    Optional<Ctdt> findByMaCt(String maCt);
    @Query("SELECT c FROM Course c JOIN c.ctdts t WHERE t.maCt = :maCt")
    Optional<Ctdt> findByName(String name);
    Optional<Ctdt> findByMaCtAndBatch(String maCt, Batch batch); // Thêm phương thức này


}
