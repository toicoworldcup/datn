package org.example.doantn.Service;

import org.example.doantn.Dto.response.UserInfoResponse;
import org.example.doantn.Entity.RoleType;
import org.example.doantn.Entity.Student;
import org.example.doantn.Entity.Teacher;
import org.example.doantn.Entity.User;
import org.example.doantn.Repository.RoleRepo;
import org.example.doantn.Repository.StudentRepo;
import org.example.doantn.Repository.TeacherRepo;
import org.example.doantn.Repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService{
    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepo studentRepo;
    private final TeacherRepo teacherRepo;



    public UserService(UserRepo userRepository, RoleRepo roleRepository, TeacherRepo teacherRepo,StudentRepo studentRepo, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;


    }

    public User registerUser(String username, String password, String roleName, String mssv, String maGv, String userType) {
        RoleType role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException(" Role không hợp lệ"));

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        if ("STUDENT".equalsIgnoreCase(userType)) {
            if (mssv == null) {
                throw new RuntimeException(" Cần cung cấp mssv cho học sinh");
            }
            Student student = studentRepo.findByMssv(mssv)
                    .orElseThrow(() -> new RuntimeException(" Không tìm thấy học sinh"));
            student.setUser(user);
            user.setStudent(student);
        }
        if ("TEACHER".equalsIgnoreCase(userType)) {
            if (maGv == null) {
                throw new RuntimeException(" Cần cung cấp ma giao vien cho giáo viên");
            }
            Teacher teacher = teacherRepo.findByMaGv(maGv)
                    .orElseThrow(() -> new RuntimeException(" Không tìm thấy giáo viên"));
            teacher.setUser(user);
            user.setTeacher(teacher);
        }

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().getName()) // Thêm "ROLE_" vào quyền
                .build();
    }

    public UserInfoResponse getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Kiểm tra loại người dùng (Student hoặc Teacher)
        String fullName = "";
        String role = user.getRole().getName();

        if (user.getStudent() != null) {
            fullName = user.getStudent().getName(); // Lấy tên học sinh
        } else if (user.getTeacher() != null) {
            fullName = user.getTeacher().getName(); // Lấy tên giáo viên
        }

        return new UserInfoResponse(fullName, role);
    }




}

