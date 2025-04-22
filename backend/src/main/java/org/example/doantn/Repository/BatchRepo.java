package org.example.doantn.Repository;

import org.example.doantn.Entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BatchRepo extends JpaRepository<Batch, Integer> {
    Optional<Batch> findByName(String name);
}
