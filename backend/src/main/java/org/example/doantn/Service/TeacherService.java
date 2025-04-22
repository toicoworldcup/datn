package org.example.doantn.Service;

import org.example.doantn.Entity.Clazz;
import org.example.doantn.Entity.Department;
import org.example.doantn.Entity.Teacher;
import org.example.doantn.Repository.DepartmentRepo;
import org.example.doantn.Repository.TeacherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepo teacherRepo;
    @Autowired
    private DepartmentRepo departmentRepo;
    public List<Teacher> getAllTeachers() {
        return teacherRepo.findAll();
    }
    public Teacher addTeacher(Teacher teacher) {
        if (teacher.getDepartment() != null && teacher.getDepartment().getId() != 0) {
            teacher.setDepartment(departmentRepo.findById(teacher.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found")));
        } else {
            teacher.setDepartment(null);
        }
        return teacherRepo.save(teacher);
    }

    public Teacher updateTeacher(int id, Teacher updatedTeacher) {
        Optional<Teacher> optionalTeacher = teacherRepo.findById(id);
        if (optionalTeacher.isPresent()) {
            Teacher existingTeacher = optionalTeacher.get();
            existingTeacher.setPhoneNumber(updatedTeacher.getPhoneNumber());
            existingTeacher.setName(updatedTeacher.getName());
            existingTeacher.setEmail(updatedTeacher.getEmail());
            existingTeacher.setMaGv(updatedTeacher.getMaGv());
            return teacherRepo.save(existingTeacher);
        } else {
            throw new RuntimeException("Không tìm thấy học viên với ID: " + id);
        }
    }

    public void deleteTeacher(int id) {
        Optional<Teacher> teacherOptional = teacherRepo.findById(id);
        if(teacherOptional.isPresent()) {
            teacherRepo.deleteById(id);
        }
    }
    public int getNumberOfClassesByTeacher(int teacherId) {
        Optional<Teacher> optionalTeacher = teacherRepo.findById(teacherId);
        if (optionalTeacher.isPresent()) {
            return teacherRepo.countclazzesByTeacher(teacherId);
        } else {
            throw new RuntimeException("Không tìm thấy học viên với ID: " + teacherId);
        }
    }
    public Set<Clazz> getClazzesByMaGv(String maGv) {
        Optional<Teacher> optionalTeacher = teacherRepo.findByMaGv(maGv);

        if (optionalTeacher.isPresent()) {
            return teacherRepo.getClazzes(maGv);
        } else {
            throw new RuntimeException("Không tìm thấy giáo viên với ma: " + maGv);
        }
    }
    public Optional<Teacher> getTeacherByUsername(String username) {
        return teacherRepo.findByUser_Username(username);
    }


}
