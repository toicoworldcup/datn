package org.example.doantn.Repository;

import org.example.doantn.Entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeslotRepo extends JpaRepository<TimeSlot, Integer> {
}
