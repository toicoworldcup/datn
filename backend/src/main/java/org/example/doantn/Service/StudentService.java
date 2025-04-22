package org.example.doantn.Service;

import org.example.doantn.Entity.Batch;
import org.example.doantn.Entity.Department;
import org.example.doantn.Entity.Student;
import org.example.doantn.Entity.Teacher;
import org.example.doantn.Repository.BatchRepo;
import org.example.doantn.Repository.DepartmentRepo;
import org.example.doantn.Repository.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StudentService {
    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private BatchRepo batchRepo;

    @Autowired
    private DepartmentRepo departmentRepo;

    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }
    public Student getStudentById(int id) {
        return studentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học viên với ID: " + id));
    }
    public Optional<Student> getStudentByMssv(String mssv) {
        return studentRepo.findByMssv(mssv);
    }


    public Student addStudent(Student student) {
        // Kiểm tra Batch ID
        if (student.getBatch() == null || student.getBatch().getId() == null) {
            throw new IllegalArgumentException(" Lỗi: Batch ID không được để trống!");
        }
        Batch batch = batchRepo.findById(student.getBatch().getId())
                .orElseThrow(() -> new IllegalArgumentException(" Lỗi: Batch ID " + student.getBatch().getId() + " không tồn tại!"));
        student.setBatch(batch);

        // Kiểm tra Department ID
        if (student.getDepartment() == null || student.getDepartment().getId() == null) {
            throw new IllegalArgumentException("Lỗi: Department ID không được để trống!");
        }
        Department department = departmentRepo.findById(student.getDepartment().getId())
                .orElseThrow(() -> new IllegalArgumentException(" Lỗi: Department ID " + student.getDepartment().getId() + " không tồn tại!"));
        student.setDepartment(department);

        return studentRepo.save(student);
    }
    public Optional<Student> getStudentByUsername(String username) {
        return studentRepo.findByUser_Username(username);
    }

    public Student updateStudent(int id, Student updatedStudent) {
        Optional<Student> optionalStudent = studentRepo.findById(id);
        if (optionalStudent.isPresent()) {
            Student existingStudent = optionalStudent.get();
            existingStudent.setAddress(updatedStudent.getAddress());
            existingStudent.setDateOfBirth(updatedStudent.getDateOfBirth());
            existingStudent.setName(updatedStudent.getName());
            existingStudent.setEmail(updatedStudent.getEmail());
            existingStudent.setPhone(updatedStudent.getPhone());
            return studentRepo.save(existingStudent);
        } else {
            throw new RuntimeException("Không tìm thấy học viên với ID: " + id);
        }
    }
    public void deleteStudent(int id) {
        Optional<Student> optionalStudent = studentRepo.findById(id);
        if (optionalStudent.isPresent()) {
            studentRepo.deleteById(id);
        }
        else {
            throw new RuntimeException("Không tìm thấy học viên với ID: " + id);
        }
    }


    public Optional<Student> searchStudents(String keyword) {
        return studentRepo.findByMssv(keyword);
    }

}
