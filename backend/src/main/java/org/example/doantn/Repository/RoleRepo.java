package org.example.doantn.Repository;

import org.example.doantn.Entity.RoleType;
import org.example.doantn.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<RoleType, Integer> {
    Optional<RoleType> findByName(String name);

}
