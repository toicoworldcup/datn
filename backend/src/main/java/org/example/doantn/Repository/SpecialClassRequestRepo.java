package org.example.doantn.Repository;

import org.example.doantn.Entity.SpecialClassRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialClassRequestRepo extends JpaRepository<SpecialClassRequest, Integer> {
    List<SpecialClassRequest> findByStudent_MssvAndClazz_MaLopAndSemester_Name(String studentMssv, String clazzMaLop, String semesterName);
    List<SpecialClassRequest> findByStatus(String status);
}