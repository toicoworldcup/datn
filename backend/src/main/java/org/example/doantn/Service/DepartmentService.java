package org.example.doantn.Service;

import org.example.doantn.Entity.Department;
import org.example.doantn.Entity.Teacher;
import org.example.doantn.Repository.DepartmentRepo;
import org.example.doantn.Repository.TeacherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepo departmentRepo;
    public List<Department> getAllDepartments() {
        return departmentRepo.findAll();
    }
    public Department addDepartment(Department department) {
        return departmentRepo.save(department);
    }
    public Department updateDepartment(int id, Department updatedDepartment) {
        Optional<Department> optionalDepartment = departmentRepo.findById(id);
        if (optionalDepartment.isPresent()) {
            Department existingDepartment = optionalDepartment.get();
            existingDepartment.setName(updatedDepartment.getName());
            return departmentRepo.save(existingDepartment);
        } else {
            throw new RuntimeException("Không tìm thấy học viên với ID: " + id);
        }
    }

    public void deleteTeacher(int id) {
        Optional<Department> departmentOptional = departmentRepo.findById(id);
        if(departmentOptional.isPresent()) {
            departmentRepo.deleteById(id);
        }
    }
}
