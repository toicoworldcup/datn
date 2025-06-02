package org.example.doantn.Service;

import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Course;
import org.example.doantn.Repository.CourseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CourseService {
    @Autowired
    private CourseRepo courseRepo;

    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }
    public Course getCourseByMaHocPhan(String maHocPhan) {
        return courseRepo.findByMaHocPhan(maHocPhan)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + maHocPhan));
    }
    public Course createCourse(Course course) {
        return courseRepo.save(course);
    }

    public Set<Clazz> getClazzesByMaHocPhan(String maHocPhan,String semesterName) {
        return courseRepo.getClazzesByMaHocPhanAndSemesterName(maHocPhan,semesterName);
    }
    public Course updateCourse(Integer id, String name, String maHocPhan, Integer tinChi) {
        Course existingCourse = courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + id));

        if (name != null) {
            existingCourse.setName(name);
        }
        if (maHocPhan != null) {
            existingCourse.setMaHocPhan(maHocPhan);
        }
        if (tinChi != null) {
            existingCourse.setTinChi(tinChi);
        }

        return courseRepo.save(existingCourse);
    }

    public void deleteCourse(Integer id) {
        if (!courseRepo.existsById(id)) {
            throw new RuntimeException("Không tìm thấy khóa học với ID: " + id);
        }
        courseRepo.deleteById(id);
    }

    public List<Course> searchCoursesByProgramAndKhoa(String program, String khoa) {
        // Triển khai logic tìm kiếm dựa trên program và khoa tại đây
        // Ví dụ (tùy thuộc vào cấu trúc Entity Course của bạn):
        if (program != null && !program.isEmpty() && khoa != null && !khoa.isEmpty()) {
            return courseRepo.findByCtdt_maCtAndKhoa(program, khoa);
        } else if (program != null && !program.isEmpty()) {
            return courseRepo.findByCtdt_maCt(program);
        } else if (khoa != null && !khoa.isEmpty()) {
            return courseRepo.findByKhoa(khoa);
        } else {
            return courseRepo.findAll(); // Hoặc trả về một danh sách trống nếu không có tiêu chí tìm kiếm
        }
    }

}
