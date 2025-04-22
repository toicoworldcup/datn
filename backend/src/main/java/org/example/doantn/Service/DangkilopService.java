package org.example.doantn.Service;

import org.example.doantn.Entity.*;
import org.example.doantn.Repository.ClazzRepo;
import org.example.doantn.Repository.DangkilopRepo;
import org.example.doantn.Repository.DangkilopRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Repository.SemesterRepo;
import org.example.doantn.Repository.StudentRepo;
import org.example.doantn.Repository.StudentRepo;
import org.example.doantn.Repository.ClazzRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class DangkilopService {
    @Autowired
    private DangkilopRepo dangkilopRepo;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private ClazzRepo clazzRepo;
    @Autowired
    private SemesterRepo semesterRepo;

    public List<Dangkilop> getAllDangkilop() {
        return dangkilopRepo.findAll();
    }

    public Dangkilop getDangkilopById(Integer id) {
        return dangkilopRepo.findById(id).orElseThrow(() -> new RuntimeException("Đăng ký không tồn tại"));
    }
    public Dangkilop registerClass(Dangkilop dangkilop) {
        Student student = studentRepo.findByMssv(dangkilop.getStudent().getMssv())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với MSSV: " ));

        Clazz clazz = clazzRepo.findByMaLopAndSemester_Name(dangkilop.getClazz().getMaLop(),dangkilop.getSemester().getName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp : " ));

        Semester semester = semesterRepo.findByName(dangkilop.getSemester().getName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kỳ học : " ));

        if (!semester.isOpen()) {
            throw new RuntimeException("Kỳ học này chưa mở đăng ký.");
        }

        boolean exists = dangkilopRepo.existsByStudentAndClazz(student, clazz);
        if (exists) {
            throw new RuntimeException("Bạn đã đăng ký lớp này trước đó.");
        }

        dangkilop.setClazz(clazz);
        dangkilop.setSemester(semester);

        return dangkilopRepo.save(dangkilop);
    }



    // Hủy đăng ký lớp
    public void deleteDangkilop(Integer id) {
        Dangkilop dangkilop = getDangkilopById(id);
        dangkilopRepo.delete(dangkilop);
    }

    public List<Dangkilop> getDangkilopByMssvAndSemester(String mssv, String semester) {
        return dangkilopRepo.findByStudent_MssvAndSemesterName(mssv, semester);
    }
}
