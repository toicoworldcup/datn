package org.example.doantn.Service;

import org.example.doantn.Dto.response.CthDTO;
import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Course;
import org.example.doantn.Entity.Ctdt;
import org.example.doantn.Entity.Student;
import org.example.doantn.Repository.CourseRepo;
import org.example.doantn.Repository.CtdtRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CtdtService {
    @Autowired
    private CtdtRepo ctdtRepo;
    @Autowired
    private CourseRepo courseRepo;
    public List<Ctdt> getAllCtdts() {
        return ctdtRepo.findAll();
    }
    public Optional<Ctdt> getCtdtByMaCt(String maCt) {return  ctdtRepo.findByMaCt(maCt);}
    public Ctdt createCtdt(Ctdt ctdt) {
        return ctdtRepo.save(ctdt);
    }
    public Ctdt assignCourse(String maCt,String maHocPhan){
        Course course = courseRepo.findByMaHocPhan(maHocPhan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học phần với mã: " ));

        Ctdt ctdt = ctdtRepo.findByMaCt(maCt)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ctdt với mã: " ));
        ctdt.getCourses().add(course);
        course.getCtdts().add(ctdt);
        return ctdtRepo.save(ctdt);

    }

    public List<CthDTO> getCoursesByMaCt(String maCt) {
        Ctdt ctdt = ctdtRepo.findByMaCt(maCt)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CTDT với maCt: " + maCt));
        return ctdt.getCourses().stream()
                .map(course -> new CthDTO(ctdt.getName(), course.getMaHocPhan(), course.getTinChi()))
                .collect(Collectors.toList());
    }



}
