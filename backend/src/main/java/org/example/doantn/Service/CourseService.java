package org.example.doantn.Service;

import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Course;
import org.example.doantn.Entity.Teacher;
import org.example.doantn.Repository.CourseRepo;
import org.example.doantn.Repository.TeacherRepo;
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

}
