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
    public Course updateCourse(Integer id, String tenMonHoc, String maHocPhan, Integer soTinChi, String khoiLuong, Integer suggestedSemester, String gradeRatio) {
        // Tìm khóa học hiện có theo ID
        Course existingCourse = courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + id));
        // Vẫn nên xem xét thay thế RuntimeException bằng một Custom Exception
        // như ResourceNotFoundException đã đề cập trước đó.

        // Cập nhật các trường của ENTITY dựa trên dữ liệu từ DTO
        // Lưu ý: Các tham số của phương thức này có tên giống với DTO để tiện mapping
        // nhưng bên trong chúng ta gọi setter của Entity với tên trường của Entity.

        if (tenMonHoc != null) {
            existingCourse.setName(tenMonHoc); // Tên môn học trong Entity là 'name'
        }
        if (maHocPhan != null) {
            existingCourse.setMaHocPhan(maHocPhan);
        }
        if (soTinChi != null) {
            existingCourse.setTinChi(soTinChi); // Số tín chỉ trong Entity là 'tinChi'
        }
        if (khoiLuong != null) {
            existingCourse.setKhoiLuong(khoiLuong);
        }
        if (suggestedSemester != null) {
            existingCourse.setSuggestedSemester(suggestedSemester);
        }
        if (gradeRatio != null) {
            existingCourse.setGradeRatio(gradeRatio);
        }

        // Lưu và trả về Course đã được cập nhật
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
